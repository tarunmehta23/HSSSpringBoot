package com.charter.provisioning.hss.service;

import com.charter.provisioning.hss.common.ServiceConstants;
import com.charter.provisioning.hss.config.HssServiceConfig;
import com.charter.provisioning.hss.exception.ServiceException;
import com.charter.provisioning.hss.factory.ServiceFactory;
import com.charter.provisioning.hss.factory.ServiceFactory.ServiceType;
import com.charter.provisioning.hss.handler.CommonSubscriberHandler;
import com.charter.provisioning.hss.model.DigitalPhone;
import com.charter.provisioning.hss.model.DigitalPhoneResponse;
import com.charter.provisioning.hss.model.PrivateIdentity;
import com.charter.provisioning.hss.model.PublicIdentity;
import com.charter.provisioning.network.hss.subscriber.spml.schema.Subscriber;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Slf4j
@Service
public class DigitalPhoneServiceImpl implements DigitalPhoneService {

	private CommonSubscriberHandler commonHandler;

	private HssServiceConfig serviceConfig;

    @Value("${privateIdentity.length}")
    private int privateIdentityLength;

	@Autowired
	public DigitalPhoneServiceImpl(CommonSubscriberHandler commonHandler, HssServiceConfig serviceConfig) {

		this.commonHandler = commonHandler;
		this.serviceConfig = serviceConfig;
	}

	/**
	 * Method creates HSS Subscriber.
	 *
	 * @param digitalPhone
	 *            digitalPhone attribute is used to add request for hss subscriber.
	 * @param transactionId
	 *            uuid used to trace a transaction through all systems end to end.
	 * @return DigitalPhoneResponse
	 */
	@Override
	public DigitalPhoneResponse createSubscriber(DigitalPhone digitalPhone, String transactionId) {

		DigitalPhoneResponse response = null;

		if (null == digitalPhone.getName() || serviceConfig.getDPhone().equalsIgnoreCase(digitalPhone.getName()))
			response = ServiceFactory.getService(ServiceType.CreateSubscriberHandler).execute(digitalPhone, transactionId);
		else if ( serviceConfig.getHGroup().equalsIgnoreCase(digitalPhone.getName()))
			response = ServiceFactory.getService(ServiceType.CreateHGSubscriberHandler).execute(digitalPhone, transactionId);
		else if (serviceConfig.getBGroup().equalsIgnoreCase(digitalPhone.getName()))
			response = ServiceFactory.getService(ServiceType.CreateBGSubscriberHandler).execute(digitalPhone, transactionId);

		return response;
	}

	/**
	 * Method deletes HSS Subscriber.
	 *
	 * @param digitalPhone
	 *            digitalPhone attribute is used to delete request for hss
	 *            subscriber.
	 * @param transactionId
	 *            uuid used to trace a transaction through all systems end to end.
	 * @return DigitalPhoneResponse
	 */
	@Override
	public DigitalPhoneResponse deleteSubscriber(DigitalPhone digitalPhone, String transactionId) {

		DigitalPhoneResponse response = null;
		if (null == digitalPhone.getName() || serviceConfig.getDPhone().equalsIgnoreCase(digitalPhone.getName()))
			response =  ServiceFactory.getService(ServiceType.DeleteSubscriberHandler).execute(digitalPhone, transactionId);
		else if ( serviceConfig.getHGroup().equalsIgnoreCase(digitalPhone.getName()))
			response = ServiceFactory.getService(ServiceType.DeleteHGSubscriberHandler).execute(digitalPhone, transactionId);
		else if (serviceConfig.getBGroup().equalsIgnoreCase(digitalPhone.getName()))
			response = ServiceFactory.getService(ServiceType.DeleteBGSubscriberHandler).execute(digitalPhone, transactionId);

		return response;
	}

	/**
	 * Method retrieves Hss Subscriber by TelephoneNumber,National PublicIdentity
	 * and PrivateIdentity.
	 *
	 * @param telephoneNumber
	 *            PublicIdentity/PhoneNumber associated with HSS subscriber.
	 * @param controllerId
	 *            Hunt Group/Business Group controller id associated with HSS subscriber.
	 * @param privateIdentity
	 *            privateIdentity associated with HSS subscriber.
	 * @param transactionId
	 *            uuid used to trace a transaction through all systems end to end.
	 * @return Subscriber
	 */
	@Override
	public Subscriber getDigitalPhoneSubscriber(String telephoneNumber, String controllerId, String privateIdentity, String transactionId) {

		Subscriber subscriber;
		log.info("[{}] - Enter getDigitalPhoneSubscriber {}", transactionId);

		// Checking for subscriber with the Public identity
		if (!StringUtils.isBlank(telephoneNumber))
			subscriber = getDigitalPhoneWithTelephoneNumber(telephoneNumber, transactionId);
		else if (!StringUtils.isBlank(controllerId))
			subscriber = getDigitalPhoneWithSubscriberId(controllerId, transactionId);
		else if (!StringUtils.isBlank(privateIdentity))
			subscriber = getDigitalPhoneWithPrivateIdentity(privateIdentity, transactionId);
		else {
			log.error("[{}] - No input passed.", transactionId);
			throw new ServiceException(HttpStatus.BAD_REQUEST.value(), " No input passed");
		}

		if (null == subscriber) {
			log.error("[{}] - Subscriber Not found for telephoneNumber: {}", transactionId, telephoneNumber);
			throw new ServiceException(HttpStatus.NOT_FOUND.value(), "Subscriber Not found");
		}
		return subscriber;
	}

	// Checking for subscriber with the Telephone Number
	private Subscriber getDigitalPhoneWithTelephoneNumber(String telephoneNumber, String transactionId) {

		Subscriber subscriber;
		if (telephoneNumber.matches(ServiceConstants.TN_VALIDATION_EXP))

			subscriber = commonHandler.searchSubscriberByPublicIdentity(
					PublicIdentity.builder().userId(telephoneNumber).build(), null, transactionId);
		else {
			log.error("[{}] - Invalid telephoneNumber {}", transactionId, telephoneNumber);
			throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "Invalid telephoneNumber");
		}
		return subscriber;
	}

	// Checking for subscriber with the Subscriber Id
	private Subscriber getDigitalPhoneWithSubscriberId(String controllerId, String transactionId) {

		Subscriber subscriber;
		if ((controllerId.startsWith(ServiceConstants.PICKUP_GROUP)
				&& StringUtils.isNumeric(controllerId.replace(ServiceConstants.PICKUP_GROUP,"")))

				|| (controllerId.startsWith(ServiceConstants.MLHG_ID)
				&& Pattern.compile(ServiceConstants.NATIONAL_PUBLIC_DELIMITER)
				.splitAsStream(controllerId.replace(ServiceConstants.MLHG_ID,"")).allMatch(StringUtils::isNumeric)))

			subscriber = commonHandler.searchSubscriberByNationalPublicIdentity(
					PublicIdentity.builder().userId(controllerId).build(), null, transactionId);
		else {
			log.error("[{}] - Invalid controllerId {}", transactionId, controllerId);
			throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "Invalid controllerId");
		}
		return subscriber;
	}

	// Checking for subscriber with the Private identity
	private Subscriber getDigitalPhoneWithPrivateIdentity(String privateIdentity, String transactionId) {

		Subscriber subscriber;
		if (privateIdentity.length() == privateIdentityLength)
			subscriber = commonHandler.searchSubscriberByPrivateIdentity(
					PrivateIdentity.builder().userId(privateIdentity).build(), null, transactionId);

		else {
			log.error("[{}] - Invalid privateIdentity {}", transactionId, privateIdentity);
			throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "Invalid privateIdentity");
		}
		return subscriber;
	}
}
