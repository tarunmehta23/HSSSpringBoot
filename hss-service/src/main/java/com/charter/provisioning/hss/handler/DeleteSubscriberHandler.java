package com.charter.provisioning.hss.handler;


import com.charter.provisioning.hss.common.DigitalPhoneCommon;
import com.charter.provisioning.hss.common.ServiceConstants;
import com.charter.provisioning.hss.config.HssServiceConfig;
import com.charter.provisioning.hss.exception.ServiceException;
import com.charter.provisioning.hss.factory.ServiceInterface;
import com.charter.provisioning.hss.model.DigitalPhone;
import com.charter.provisioning.hss.model.DigitalPhoneResponse;
import com.charter.provisioning.hss.model.DigitalPhoneResponse.Status;
import com.charter.provisioning.hss.model.PrivateIdentity;
import com.charter.provisioning.hss.model.PublicIdentity;
import com.charter.provisioning.network.hss.subscriber.spml.DeleteRequest;
import com.charter.provisioning.network.hss.subscriber.spml.ModifyRequest;
import com.charter.provisioning.network.hss.subscriber.spml.SpmlResponse;
import com.charter.provisioning.network.hss.subscriber.spml.schema.Subscriber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DeleteSubscriberHandler implements ServiceInterface{

	private CommonSubscriberHandler commonHandler;

	private HssServiceConfig serviceConfig;

	private DigitalPhoneCommon digitalPhoneCommon;
	
	@Autowired
	public DeleteSubscriberHandler(CommonSubscriberHandler commonHandler, HssServiceConfig serviceConfig, DigitalPhoneCommon digitalPhoneCommon) {
		super();
		this.commonHandler = commonHandler;
		this.serviceConfig = serviceConfig;
		this.digitalPhoneCommon = digitalPhoneCommon;
	}

	/* Deletes Existing HSS Subscriber
	 * @see com.charter.provisioning.hss.factory.ServiceInterface#execute(com.charter.provisioning.hss.model.DigitalPhone, java.lang.String)
	 */
	@Override
	public DigitalPhoneResponse execute(DigitalPhone digitalPhone, String transactionId) {

		if (null != digitalPhone) {
			log.info("Enter method CreateSubscriberHandler");

			// Input validation check.
			validatePublicIdentity(digitalPhone, transactionId);
			

			// Checking for subscriber with the first Public identity
			Subscriber subscriber = commonHandler.searchSubscriberByPublicIdentity(
					digitalPhone.getPublicIdentity().get(0), digitalPhone.getSite(), transactionId);

			if (subscriber != null) {
				List<String> allTNsToDelete = digitalPhone.getPublicIdentity().stream()
						.filter(p -> serviceConfig.getOperationDelete().equalsIgnoreCase(p.getOperation()))
						.map(PublicIdentity::getUserId).collect(Collectors.toList());

				if (!CollectionUtils.isEmpty(allTNsToDelete)) {
					if (isLastPublicIdentity(subscriber, allTNsToDelete, transactionId)) {
						return commonHandler
								.createResponse(deleteSubscriber(subscriber.getIdentifier(), transactionId));
					} else {
						log.info("Deleting Secondary Line for telephoneNumber :{}", allTNsToDelete);
						return commonHandler.createResponse(deletePublicIdentity(subscriber, allTNsToDelete,
								digitalPhone.getPrivateIdentity(), transactionId));
					}
				} else {
					log.error("No Telephone number's passed in request for delete");
					throw new ServiceException(HttpStatus.NOT_FOUND.value(),
							"No Telephone number's passed in request to be deleted");
				}
			} else {
				log.error("could not found subscriber for passed in telephoneNumber :{}",
						digitalPhone.getPublicIdentity().get(0).getUserId());
				throw new ServiceException(HttpStatus.NOT_FOUND.value(), "Could not found subscriber");
			}
		}
		return DigitalPhoneResponse.builder().status(Status.SUCCESS).build();
	}

	private SpmlResponse deleteSubscriber(String identifier, String transactionId) {
		
		DeleteRequest deleteRequest = commonHandler.createDeleteRequest(identifier);
		return commonHandler.processSpmlRequest(deleteRequest, transactionId);
	}

	private SpmlResponse deletePublicIdentity(Subscriber subscriber, List<String> allTNsToDelete, List<PrivateIdentity> privateIdentityList, String transactionId) {

		if (null == subscriber.getHss() || CollectionUtils.isEmpty(subscriber.getHss().getPublicUserId())) {
			log.error("[{}] - The DN Subscriber doesn't exists.", transactionId);
			throw new ServiceException(HttpStatus.NOT_FOUND.value(), "Subscriber Not found");
		}

		ModifyRequest modifyRequest = createRemoveSecondaryLineRequest(subscriber, allTNsToDelete, privateIdentityList, transactionId);
		return commonHandler.processSpmlRequest(modifyRequest, transactionId);
	}

	private boolean isLastPublicIdentity(Subscriber subscriber, List<String> allTNsToDelete, String transactionId) {
		
		Set<String> subscriberUserIdSet = new HashSet<>();
		
		if (null == subscriber.getHss() || CollectionUtils.isEmpty(subscriber.getHss().getPublicUserId()))
			return true;
		
		subscriber.getHss().getPublicUserId().forEach(p -> {
			if (null != p && null != p.getOriginalPublicUserId()
					&& Boolean.TRUE.toString().equalsIgnoreCase(p.getDefaultIndication())) {

				String tn = null;
				if (p.getOriginalPublicUserId().length() >= 16 && p.getOriginalPublicUserId()
						.startsWith(serviceConfig.getSipPrefix().concat(serviceConfig.getE164DigitPrefix()))) {
					
					tn = p.getOriginalPublicUserId().substring(6, 16);
					
				} else if (p.getOriginalPublicUserId().length() >= 14
						&& p.getOriginalPublicUserId().startsWith(serviceConfig.getSipPrefix())) {
					
					tn = p.getOriginalPublicUserId().substring(4, 14);
				}
				if (null != tn && tn.matches(ServiceConstants.TN_VALIDATION_EXP))
					subscriberUserIdSet.add(tn);
			}
		});
		
		log.debug("[{}] - subscriberUserIdSet = {} ",transactionId, subscriberUserIdSet);
		if (!CollectionUtils.isEmpty(allTNsToDelete))
			subscriberUserIdSet.removeAll(allTNsToDelete);
		
		log.debug("[{}] - subscriberUserIdSet after removing allTNsToDelete = {} ",transactionId, subscriberUserIdSet);
		return CollectionUtils.isEmpty(subscriberUserIdSet);
	}

	private ModifyRequest createRemoveSecondaryLineRequest(Subscriber subscriber, List<String> allTNsToDelete,
			List<PrivateIdentity> privateIdentityList, String transactionId) {

		ModifyRequest modifyRequest = digitalPhoneCommon.createModifyRequest(subscriber.getIdentifier());

		subscriber.getHss().getPublicUserId().forEach(publicUserId -> allTNsToDelete.stream()
				.filter(tn -> publicUserId.getOriginalPublicUserId().contains(tn)).forEach(p -> {

					modifyRequest.addModification(digitalPhoneCommon.createPublicUserDeleteModification(publicUserId));
					modifyRequest.addModification(
							digitalPhoneCommon.createServiceProfileModification(publicUserId.getServiceProfileName()));
					modifyRequest.addModification(digitalPhoneCommon.createIRSModification(publicUserId.getIrsId()));
				}));

		// Populating privateIdenity to be removed for the secondary line.
		if (!CollectionUtils.isEmpty(privateIdentityList)) {
			privateIdentityList.forEach(privateIdentity -> modifyRequest
					.addModification(digitalPhoneCommon.createPrivateIdentityModification(privateIdentity)));
		}
		return modifyRequest;
	}
	
	private void validatePublicIdentity(DigitalPhone digitalPhone, String transactionId) {

		log.debug("[{}] - Validating public identity.", transactionId);
		if (digitalPhone.getPublicIdentity().size() > 1) {
			log.error("[{}] - Multiple Public identities found in request.", transactionId);
			throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "Multiple Public identities found in request.");
		}

		String phoneNumber = digitalPhone.getPublicIdentity().get(0).getUserId();
		if (StringUtils.isEmpty(phoneNumber) || !phoneNumber.matches(ServiceConstants.TN_VALIDATION_EXP)) {
			log.error("Invalid Phone Number passed in telephoneNumber :{}", phoneNumber);
			throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "Invalid Phone Number passed");
		}
	}
}
