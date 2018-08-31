package com.charter.provisioning.hss.handler;

import com.charter.provisioning.hss.common.DigitalPhoneCommon;
import com.charter.provisioning.hss.common.ServiceConstants;
import com.charter.provisioning.hss.config.HssServiceConfig;
import com.charter.provisioning.hss.exception.ServiceException;
import com.charter.provisioning.hss.factory.ServiceInterface;
import com.charter.provisioning.hss.model.DigitalPhone;
import com.charter.provisioning.hss.model.DigitalPhoneResponse;
import com.charter.provisioning.hss.model.PublicIdentity;
import com.charter.provisioning.network.hss.subscriber.spml.DeleteRequest;
import com.charter.provisioning.network.hss.subscriber.spml.ModifyRequest;
import com.charter.provisioning.network.hss.subscriber.spml.SpmlResponse;
import com.charter.provisioning.network.hss.subscriber.spml.schema.PublicUserId;
import com.charter.provisioning.network.hss.subscriber.spml.schema.Subscriber;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DeleteHGSubscriberHandler implements ServiceInterface {

	private CommonSubscriberHandler commonHandler;

	private HssServiceConfig serviceConfig;

	private DigitalPhoneCommon digitalPhoneCommon;

	private final String HG_CONTROLLER_GROUP = "0000";

	@Autowired
	public DeleteHGSubscriberHandler(CommonSubscriberHandler commonHandler, HssServiceConfig serviceConfig,
			DigitalPhoneCommon digitalPhoneCommon) {
		super();
		this.commonHandler = commonHandler;
		this.serviceConfig = serviceConfig;
		this.digitalPhoneCommon = digitalPhoneCommon;
	}

	/*
	 * Deletes Existing HSS Subscriber
	 * @see com.charter.provisioning.hss.factory.ServiceInterface#execute(com.charter.provisioning.hss.model.DigitalPhone, java.lang.String)
	 */
	@Override
	public DigitalPhoneResponse execute(DigitalPhone digitalPhone, String transactionId) {

		if (null != digitalPhone) {
			log.info("Enter method DeleteHGSubscriberHandler");

			// Input validation check.
			validateHGPublicIdentity(digitalPhone, transactionId);

			// Checking for subscriber with the first Public identity
			Subscriber controllerSubscriber = commonHandler.searchSubscriberByNationalPublicIdentity(
					getHGControllerPublicIdentity(digitalPhone, transactionId), digitalPhone.getSite(), transactionId);
			if (null != controllerSubscriber) {
				return commonHandler.createResponse(
						deleteHGControllerSubscriber(controllerSubscriber.getIdentifier(), transactionId));
			} else {
				Subscriber terminalSubscriber = commonHandler.searchSubscriberByNationalPublicIdentity(
						getSubscriberByHGPublicIdentity(digitalPhone, transactionId), digitalPhone.getSite(),
						transactionId);
				if (null != terminalSubscriber) {
					return commonHandler.createResponse(
							deleteHGTerminalSubscriber(digitalPhone, terminalSubscriber, transactionId));
				} else {
					log.error("could not found subscriber for passed in mlHg Id :{}",
							digitalPhone.getPublicIdentity().get(0).getUserId());
					throw new ServiceException(HttpStatus.NOT_FOUND.value(), "Could not found subscriber");
				}
			}
		}
		return DigitalPhoneResponse.builder().status(DigitalPhoneResponse.Status.FAILURE).build();
	}

	private PublicIdentity getHGControllerPublicIdentity(DigitalPhone digitalPhone, String transactionId) {

		List<PublicIdentity> publicIdentityList = digitalPhone.getPublicIdentity().stream()
				.filter(p -> serviceConfig.getOperationDelete().equalsIgnoreCase(p.getOperation())
						&& p.getUserId().endsWith(HG_CONTROLLER_GROUP))
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(publicIdentityList)) {
			log.debug("[{}] - No controller public id found for HG request.", transactionId);
			return null;
		}
		return publicIdentityList.get(0);
	}

	private PublicIdentity getSubscriberByHGPublicIdentity(DigitalPhone digitalPhone, String transactionId) {

		List<PublicIdentity> publicIdentityList = digitalPhone.getPublicIdentity().stream()
				.filter(p -> serviceConfig.getOperationDelete().equalsIgnoreCase(p.getOperation())
						&& !p.getUserId().endsWith(HG_CONTROLLER_GROUP))
				.collect(Collectors.toList());

		if (CollectionUtils.isEmpty(publicIdentityList)) {
			log.debug("[{}] - No subscriber public id found for HG request.", transactionId);
			return null;
		}
		return publicIdentityList.get(0);
	}

	private SpmlResponse deleteHGControllerSubscriber(String identifier, String transactionId) {

		DeleteRequest deleteRequest = commonHandler.createDeleteRequest(identifier);
		return commonHandler.processSpmlRequest(deleteRequest, transactionId);
	}

	private SpmlResponse deleteHGTerminalSubscriber(DigitalPhone digitalPhone, Subscriber subscriber,
			String transactionId) {

		ModifyRequest modifyRequest = digitalPhoneCommon.createModifyRequest(subscriber.getIdentifier());

		digitalPhone.getPublicIdentity().stream()
				.filter(p -> serviceConfig.getOperationDelete().equalsIgnoreCase(p.getOperation())
						&& !p.getUserId().endsWith(HG_CONTROLLER_GROUP))
				.forEach(publicIdentity -> {

					PublicUserId publicUserId = findPublicUserId(publicIdentity, subscriber, transactionId);
					if (null != publicUserId)
						modifyRequest
								.addModification(digitalPhoneCommon.createPublicUserDeleteModification(publicUserId));
				});
		return commonHandler.processSpmlRequest(modifyRequest, transactionId);
	}

	private PublicUserId findPublicUserId(PublicIdentity publicIdentity, Subscriber subscriber, String transactionId) {

		if (null == subscriber.getHss() || CollectionUtils.isEmpty(subscriber.getHss().getPublicUserId())) {
			log.error("[{}] - The DN Subscriber doesn't exists.", transactionId);
			throw new ServiceException(HttpStatus.NOT_FOUND.value(), "Subscriber Not found");
		}
		for (PublicUserId p : subscriber.getHss().getPublicUserId()) {
			if (p.getOriginalPublicUserId().contains(publicIdentity.getUserId()))
				return p;
		}
		return null;
	}

	private void validateHGPublicIdentity(DigitalPhone digitalPhone, String transactionId) {

		log.debug("[{}] - Validating HG public identity.", transactionId);
		if (digitalPhone.getPublicIdentity().size() > 1) {
			log.error("[{}] - Multiple Public identities found in request.", transactionId);
			throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "Multiple Public identities found in request.");
		}

		String mlHgId = digitalPhone.getPublicIdentity().get(0).getUserId();
		if (!(mlHgId.startsWith(ServiceConstants.MLHG_ID)
				&& Pattern.compile(ServiceConstants.NATIONAL_PUBLIC_DELIMITER)
						.splitAsStream(mlHgId.replace(ServiceConstants.MLHG_ID, "")).allMatch(StringUtils::isNumeric)
				&& mlHgId.substring(mlHgId.lastIndexOf(ServiceConstants.NATIONAL_PUBLIC_DELIMITER), mlHgId.length() - 1)
						.length() == 4)) {

			log.error("[{}] - Invalid mlHg Id passed :{}", transactionId, mlHgId);
			throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "Invalid mlHg Id passed.");
		}
	}
}
