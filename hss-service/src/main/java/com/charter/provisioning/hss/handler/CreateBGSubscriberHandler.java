package com.charter.provisioning.hss.handler;

import com.charter.provisioning.hss.common.DigitalPhoneCommon;
import com.charter.provisioning.hss.common.ServiceConstants;
import com.charter.provisioning.hss.config.HssServiceConfig;
import com.charter.provisioning.hss.exception.ServiceException;
import com.charter.provisioning.hss.factory.ServiceInterface;
import com.charter.provisioning.hss.model.*;
import com.charter.provisioning.network.hss.subscriber.spml.AddRequest;
import com.charter.provisioning.network.hss.subscriber.spml.SpmlResponse;
import com.charter.provisioning.network.hss.subscriber.spml.schema.Subscriber;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Component
public class CreateBGSubscriberHandler implements ServiceInterface {

	private CommonSubscriberHandler commonHandler;

	private DigitalPhoneCommon digitalPhoneCommon;

	private HssServiceConfig serviceConfig;

	@Autowired
	public CreateBGSubscriberHandler(CommonSubscriberHandler commonHandler, DigitalPhoneCommon digitalPhoneCommon,
			HssServiceConfig serviceConfig) {
		this.commonHandler = commonHandler;
		this.digitalPhoneCommon = digitalPhoneCommon;
		this.serviceConfig = serviceConfig;
	}

	/*
	 * Creates new HSS Business Group Subscriber.
	 * 
	 * @see
	 * com.charter.provisioning.hss.factory.ServiceInterface#execute(com.charter.
	 * provisioning.hss.model.DigitalPhone, java.lang.String)
	 */
	@Override
	public DigitalPhoneResponse execute(DigitalPhone digitalPhone, String transactionId) {

		if (null != digitalPhone) {

			log.info("[{}] - Enter method CreateBGSubscriberHandler", transactionId);

			PublicIdentity nationalPublicIdentity = digitalPhone.getPublicIdentity().stream()
					.filter(p -> p.getUserId().startsWith(ServiceConstants.PICKUP_GROUP)
							&& StringUtils.isNumeric(p.getUserId().replace(ServiceConstants.PICKUP_GROUP, "")))
					.findAny().orElse(null);

			if (null == nationalPublicIdentity) {
				log.error("[{}] - Invalid pickup group passed in input.", transactionId);
				throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "Invalid pickup group passed in input.");
			}

			// Checking for subscriber with the national Public identity
			Subscriber subscriber = commonHandler.searchSubscriberByNationalPublicIdentity(nationalPublicIdentity,
					digitalPhone.getSite(), transactionId);
			if (null != subscriber) {
				log.error("[{}] - HSS HG Subscriber already exists.", transactionId);
				throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "HSS BG Subscriber already exists.");
			}

			return commonHandler.createResponse(createBGSubscriber(digitalPhone, transactionId));
		}
		return DigitalPhoneResponse.builder().status(DigitalPhoneResponse.Status.FAILURE).build();
	}

	private SpmlResponse createBGSubscriber(DigitalPhone digitalPhone, String transactionId) {

		DigitalPhone bgDigitalPhone = prepareBGDigitalPhone(digitalPhone, transactionId);
		AddRequest addRequest = digitalPhoneCommon.createAddRequest(bgDigitalPhone);

		return commonHandler.processSpmlRequest(addRequest, transactionId);
	}

	private DigitalPhone prepareBGDigitalPhone(DigitalPhone digitalPhone, String transactionId) {

		return DigitalPhone.builder().name(digitalPhone.getName())
				.profile(prepareDigitalPhoneProfile(digitalPhone, transactionId))
				.publicIdentity(digitalPhone.getPublicIdentity()).privateIdentity(createPrivateIdentity()).build();
	}

	private Profile prepareDigitalPhoneProfile(DigitalPhone digitalPhone, String transactionId) {

		Profile profile = digitalPhone.getProfile();

		if (CollectionUtils.isEmpty(profile.getFeatures()))
			profile.setFeatures(digitalPhoneCommon.getFeaturesByName(serviceConfig.getBGroup()));
		else
			log.info("[{}] - Features passed for Business Group {} ", transactionId, profile.getFeatures());

		return profile;
	}

	private List<PrivateIdentity> createPrivateIdentity() {

		String id = digitalPhoneCommon.generate16CharRandomKey();
		String digestKey = digitalPhoneCommon.generate32CharTimestampRandomKey();

		return Stream.of(PrivateIdentity.builder().userId(id).password(digestKey)
				.operation(serviceConfig.getOperationCreate()).build()).collect(Collectors.toList());
	}
}
