package com.charter.provisioning.hss.handler;

import com.charter.provisioning.hss.common.DigitalPhoneCommon;
import com.charter.provisioning.hss.common.ServiceConstants;
import com.charter.provisioning.hss.config.HssServiceConfig;
import com.charter.provisioning.hss.exception.ServiceException;
import com.charter.provisioning.hss.factory.ServiceInterface;
import com.charter.provisioning.hss.model.*;
import com.charter.provisioning.network.hss.subscriber.spml.AddRequest;
import com.charter.provisioning.network.hss.subscriber.spml.ModifyRequest;
import com.charter.provisioning.network.hss.subscriber.spml.SpmlResponse;
import com.charter.provisioning.network.hss.subscriber.spml.schema.Modification;
import com.charter.provisioning.network.hss.subscriber.spml.schema.PublicUserId;
import com.charter.provisioning.network.hss.subscriber.spml.schema.Subscriber;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class CreateHGSubscriberHandler implements ServiceInterface {

	private CommonSubscriberHandler commonHandler;

	private DigitalPhoneCommon digitalPhoneCommon;

	private HssServiceConfig serviceConfig;

	private final String HG_CONTROLLER_GROUP = "0000";

	@Autowired
	public CreateHGSubscriberHandler(CommonSubscriberHandler commonHandler, DigitalPhoneCommon digitalPhoneCommon,
			HssServiceConfig serviceConfig) {
		this.commonHandler = commonHandler;
		this.digitalPhoneCommon = digitalPhoneCommon;
		this.serviceConfig = serviceConfig;
	}

	/*
	 * Creates new HSS Hunt Group Subscriber.
	 * 
	 * @see
	 * com.charter.provisioning.hss.factory.ServiceInterface#execute(com.charter.
	 * provisioning.hss.model.DigitalPhone, java.lang.String)
	 */
	@Override
	public DigitalPhoneResponse execute(DigitalPhone digitalPhone, String transactionId) {

		if (null != digitalPhone) {

			log.info("[{}] - Enter method CreateHGSubscriberHandler", transactionId);

			PublicIdentity publicIdentity = digitalPhone.getPublicIdentity().stream()
					.filter(p -> p.getUserId().matches(ServiceConstants.TN_VALIDATION_EXP)).findAny().orElse(null);

			// Checking for subscriber with the Public identity/telephoneNumber
			Subscriber dnSubscriber = commonHandler.searchSubscriberByPublicIdentity(publicIdentity,
					digitalPhone.getSite(), transactionId);
			if (null == dnSubscriber) {
				log.error("[{}] - The DN Subscriber doesn't exists.", transactionId);
				throw new ServiceException(HttpStatus.NOT_FOUND.value(), "Subscriber Not found");
			}

			PublicIdentity nationalPublicIdentity = digitalPhone.getPublicIdentity().stream()
					.filter(p -> p.getUserId().startsWith(ServiceConstants.MLHG_ID)
							&& Pattern.compile(ServiceConstants.NATIONAL_PUBLIC_DELIMITER)
									.splitAsStream(p.getUserId().replace(ServiceConstants.MLHG_ID, ""))
									.allMatch(StringUtils::isNumeric))
					.findAny().orElse(null);

			if (null == nationalPublicIdentity) {
				log.error("[{}] - All invalid MLhg Id's passed in input.", transactionId);
				throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "All invalid mlHg Id's passed in input.");
			}

			// Checking for subscriber with the national Public identity
			Subscriber subscriber = commonHandler.searchSubscriberByNationalPublicIdentity(nationalPublicIdentity,
					digitalPhone.getSite(), transactionId);
			if (null != subscriber) {
				log.error("[{}] - HSS HG Subscriber already exists.", transactionId);
				throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "HSS HG Subscriber already exists.");
			}

			return commonHandler.createResponse(createHGSubscriber(digitalPhone, dnSubscriber, transactionId));
		}
		return DigitalPhoneResponse.builder().status(DigitalPhoneResponse.Status.FAILURE).build();
	}

	private SpmlResponse createHGSubscriber(DigitalPhone digitalPhone, Subscriber dnSubscriber, String transactionId) {

		DigitalPhone hgControllerDigitalPhone = prepareHGControllerDigitalPhone(digitalPhone, transactionId);
		AddRequest addRequest = commonHandler.createAddRequest(hgControllerDigitalPhone);

		SpmlResponse hgControllerResponse = commonHandler.processSpmlRequest(addRequest, transactionId);
		if (null == hgControllerResponse
				|| !DigitalPhoneResponse.Status.SUCCESS.name().equalsIgnoreCase(hgControllerResponse.getResult())) {
			log.error("[{}] - Failure to create HGControllerSubscriber.", transactionId);
			throw new ServiceException("Failure to create HGControllerSubscriber.");
		}
		log.debug("[{}] - Successfully created HG Controller", transactionId);

		ModifyRequest modifyRequest = createHGTerminalsSubscriberRequest(digitalPhone, dnSubscriber, transactionId);
		return commonHandler.processSpmlRequest(modifyRequest, transactionId);
	}

	private DigitalPhone prepareHGControllerDigitalPhone(DigitalPhone digitalPhone, String transactionId) {

		return DigitalPhone.builder().name(digitalPhone.getName())
				.profile(prepareDigitalPhoneProfile(digitalPhone, transactionId))
				.publicIdentity(getHGControllerPublicIdentity(digitalPhone, transactionId))
				.privateIdentity(createPrivateIdentity()).build();

	}

	private Profile prepareDigitalPhoneProfile(DigitalPhone digitalPhone, String transactionId) {

		Profile profile = digitalPhone.getProfile();

		if (CollectionUtils.isEmpty(profile.getFeatures()))
			profile.setFeatures(digitalPhoneCommon.getFeaturesByName(serviceConfig.getHGroup()));
		else
			log.info("[{}] - Features passed for Hunt Group {} ", transactionId, profile.getFeatures());

		return profile;
	}

	private List<PublicIdentity> getHGControllerPublicIdentity(DigitalPhone digitalPhone, String transactionId) {

		PublicIdentity publicIdentity = digitalPhone.getPublicIdentity().stream()
				.filter(p -> (serviceConfig.getOperationCreate().equalsIgnoreCase(p.getOperation())
						|| serviceConfig.getOperationUpdate().equalsIgnoreCase(p.getOperation()))
						&& p.getUserId().endsWith(HG_CONTROLLER_GROUP))
				.findAny().orElse(null);

		if (null == publicIdentity) {
			log.error("[{}] - Missing controller public id for HG request.", transactionId);
			throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "Missing controller public id for HG request.");
		}
		return Stream.of(publicIdentity).collect(Collectors.toList());
	}

	private List<PrivateIdentity> createPrivateIdentity() {

		String id = digitalPhoneCommon.generate16CharRandomKey();
		String digestKey = digitalPhoneCommon.generate32CharTimestampRandomKey();

		return Stream.of(PrivateIdentity.builder().userId(id).password(digestKey)
				.operation(serviceConfig.getOperationCreate()).build()).collect(Collectors.toList());
	}

	private ModifyRequest createHGTerminalsSubscriberRequest(DigitalPhone digitalPhone, Subscriber dnSubscriber,
			String transactionId) {

		List<Modification> modificationList = new ArrayList<>();
		ModifyRequest modifyRequest = digitalPhoneCommon.createModifyRequest(dnSubscriber.getIdentifier());

		digitalPhone.getPublicIdentity().stream()
				.filter(p -> serviceConfig.getOperationCreate().equalsIgnoreCase(p.getOperation())
						// Check for a valid mlHg ID
						&& p.getUserId().startsWith(ServiceConstants.MLHG_ID)
						&& Pattern.compile(ServiceConstants.NATIONAL_PUBLIC_DELIMITER)
								.splitAsStream(p.getUserId().replace(ServiceConstants.MLHG_ID, ""))
								.allMatch(StringUtils::isNumeric)
						// Avoid mlHg Id ending with controller group as that is controller Id and gets created separately.
						&& !p.getUserId().endsWith(HG_CONTROLLER_GROUP))
				.forEach(publicIdentity -> {

					PublicUserId publicUserId = findHGPublicUserId(publicIdentity, dnSubscriber, transactionId);
					if (null == publicUserId) {
						log.error("[{}] - Could not found publicUserId for passed phoneNumber: {}.", transactionId,
								publicIdentity.getServiceId());
					} else {
						log.debug("[{}] - HG publicUserId found for :: {}", transactionId, publicUserId);
						Modification modification = digitalPhoneCommon.createPublicIdentityModification(publicIdentity,
								publicUserId.getServiceProfileName(), publicUserId.getIrsId(),
								Boolean.FALSE.toString());
						modificationList.add(modification);
					}
				});
		if (!CollectionUtils.isEmpty(modificationList))
			modifyRequest.setModification(modificationList);

		return modifyRequest;
	}

	private PublicUserId findHGPublicUserId(PublicIdentity publicIdentity, Subscriber subscriber,
			String transactionId) {

		if (null == subscriber.getHss() || CollectionUtils.isEmpty(subscriber.getHss().getPublicUserId())) {
			log.error("[{}] - The DN Subscriber doesn't exists.", transactionId);
			throw new ServiceException(HttpStatus.NOT_FOUND.value(), "Subscriber Not found");
		}
		return subscriber.getHss().getPublicUserId().stream()
				.filter(p -> p.getOriginalPublicUserId().contains(publicIdentity.getServiceId())).findFirst()
				.orElse(null);
	}
}
