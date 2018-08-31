package com.charter.provisioning.hss.handler;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.charter.provisioning.hss.common.DigitalPhoneCommon;
import com.charter.provisioning.hss.common.SoapMessage;
import com.charter.provisioning.hss.config.SerializableConfig;
import com.charter.provisioning.hss.config.SpmlConfig;
import com.charter.provisioning.hss.exception.ServiceException;
import com.charter.provisioning.hss.exception.SoapServiceException;
import com.charter.provisioning.hss.external.HSSSubscriberProxy;
import com.charter.provisioning.hss.model.DigitalPhone;
import com.charter.provisioning.hss.model.DigitalPhoneResponse;
import com.charter.provisioning.hss.model.DigitalPhoneResponse.Status;
import com.charter.provisioning.hss.model.PrivateIdentity;
import com.charter.provisioning.hss.model.PublicIdentity;
import com.charter.provisioning.network.hss.subscriber.spml.AddRequest;
import com.charter.provisioning.network.hss.subscriber.spml.DeleteRequest;
import com.charter.provisioning.network.hss.subscriber.spml.SearchRequest;
import com.charter.provisioning.network.hss.subscriber.spml.SpmlRequest;
import com.charter.provisioning.network.hss.subscriber.spml.SpmlResponse;
import com.charter.provisioning.network.hss.subscriber.spml.schema.Subscriber;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CommonSubscriberHandler {

	private SoapMessage soapMessage;

	private SpmlConfig spmlConfig;

	private DigitalPhoneCommon commonService;

	private SerializableConfig serializableConfig;

	private HSSSubscriberProxy subscriberProxy;

	@Autowired
	public CommonSubscriberHandler(SoapMessage soapMessage, SpmlConfig spmlConfig, DigitalPhoneCommon commonService,
			SerializableConfig serializableConfig, HSSSubscriberProxy subscriberProxy) {
		super();
		this.soapMessage = soapMessage;
		this.spmlConfig = spmlConfig;
		this.commonService = commonService;
		this.serializableConfig = serializableConfig;
		this.subscriberProxy = subscriberProxy;
	}

	/**
	 * Common Method for Searching subscriber by Public Identity.
	 *
	 * @param publicIdentity
	 *            PublicIdentity/PhoneNumber is an attribute associated with HSS
	 *            subscriber for residential and commercial packages.
	 * @param site
	 *            site is used to determines the search domain.
	 * @param transactionId
	 *            uuid used to trace a transaction through all systems end to end.
	 * @return Subscriber
	 */
	public Subscriber searchSubscriberByPublicIdentity(PublicIdentity publicIdentity, String site,
			String transactionId) {

		Subscriber subscriber = null;
		if (null != publicIdentity) {
			log.debug("[{}] - Enter method searchSubscriberByPublicIdentity", transactionId);

			SpmlResponse spmlSearchResponse = searchSubscriber(spmlConfig.getPublic_identity_search_name(),
					publicIdentity.getUserId(), site, transactionId, true);
			if (spmlSearchResponse != null && Status.SUCCESS.name().equalsIgnoreCase(spmlSearchResponse.getResult())) {
				subscriber = spmlSearchResponse.getSubscriber();
			}
		}
		return subscriber;
	}

	/**
	 * Common Method for Searching subscriber by National Public Identity
	 *
	 * @param publicIdentity
	 *            NationalPublicIdentity/PhoneNumber is an attribute associated with
	 *            HSS subscriber for HG and BG.
	 * @param site
	 *            site is used to determines the search domain.
	 * @param transactionId
	 *            uuid used to trace a transaction through all systems end to end.
	 * @return Subscriber
	 */
	public Subscriber searchSubscriberByNationalPublicIdentity(PublicIdentity publicIdentity, String site,
			String transactionId) {

		Subscriber subscriber = null;
		if (null != publicIdentity) {
			log.debug("[{}] - Enter method searchSubscriberByNationalPublicIdentity", transactionId);

			SpmlResponse spmlSearchResponse = searchSubscriber(spmlConfig.getPublic_identity_search_name(),
					publicIdentity.getUserId(), site, transactionId, false);
			if (spmlSearchResponse != null && Status.SUCCESS.name().equalsIgnoreCase(spmlSearchResponse.getResult())) {
				subscriber = spmlSearchResponse.getSubscriber();
			}
		}
		return subscriber;
	}

	/**
	 * Common Method for Searching subscriber by Private Identity.
	 *
	 * @param privateIdentity
	 *            PrivateIdentity associated with the HSS subscriber.
	 * @param site
	 *            site is used to determines the search domain.
	 * @param transactionId
	 *            uuid used to trace a transaction through all systems end to end.
	 * @return Subscriber
	 */
	public Subscriber searchSubscriberByPrivateIdentity(PrivateIdentity privateIdentity, String site,
			String transactionId) {

		Subscriber subscriber = null;
		if (null != privateIdentity) {
			log.debug("[{}] - Enter method searchSubscriberByPrivateIdentity", transactionId);

			SpmlResponse spmlSearchResponse = searchSubscriber(spmlConfig.getPrivate_identity_search_name(),
					privateIdentity.getUserId(), site, transactionId, false);
			if (spmlSearchResponse != null && Status.SUCCESS.name().equalsIgnoreCase(spmlSearchResponse.getResult())) {
				subscriber = spmlSearchResponse.getSubscriber();
			}
		}
		return subscriber;
	}

	/**
	 * Common Method for processing all Spml Requests
	 *
	 * @param spmlRequest
	 *            Request which will be passed to HSS Subscriber.
	 * @param transactionId
	 *            uuid used to trace a transaction through all systems end to end.
	 * @return SpmlResponse
	 */
	public SpmlResponse processSpmlRequest(SpmlRequest spmlRequest, String transactionId) {

		SpmlResponse response;
		try {
			log.debug("[{}] - Enter processSpmlRequest", transactionId);

			// Marshaling request from smplRequest to string.
			String strRequest = serializableConfig.marshall(spmlRequest);
			log.debug("[{}] - Printing the Converted xml :: {}", transactionId, strRequest);

			// Create soap message for calling HSS subscriber service.
			soapMessage.populateMessageBody(strRequest);

			// Adding soap message properties
			soapMessage = commonService.populateSoapMessageProperties(soapMessage);

			// Calling proxy to call HSS subscriber service.
			String spmlResponse = subscriberProxy.sendAndReceive(soapMessage, transactionId);

			// unmarshalling response from HSS subscriber service.
			if (!StringUtils.isEmpty(spmlResponse)) {
				response = serializableConfig.unmarshall(spmlResponse);
			} else {
				// set failed response.
				response = new SpmlResponse(null, null, null, null, null, null, "failure", null, null, null);
			}

		} catch (SoapServiceException e) {
			log.error("Soap Exception occurred", e);
			throw new ServiceException("Soap Exception occurred", e);
		} catch (Exception e) {
			log.error("Unknown Soap Exception occurred", e);
			throw new ServiceException("Unknown Exception occurred", e);
		}
		return response;
	}
	
	/**
	 * Common Method to retrieve DigitalPhone Response.
	 *
	 * @param spmlResponse
	 *            Response received from HSS Subscriber.
	 * @return DigitalPhoneResponse
	 */
	public DigitalPhoneResponse createResponse(SpmlResponse spmlResponse) {

		return DigitalPhoneResponse.builder().description(spmlResponse.getErrorMessage()).status(
				spmlResponse.getResult().equalsIgnoreCase(Status.SUCCESS.name()) ? Status.CREATED : Status.FAILURE)
				.build();
	}

	/**
	 * Method used to create AddRequest for HSS Subscriber.
	 *
	 * @param digitalPhone
	 *            digitalPhone request used to create an AddRequest.
	 * @return AddRequest
	 */
	public AddRequest createAddRequest(DigitalPhone digitalPhone) {
		return commonService.createAddRequest(digitalPhone);
	}

	SpmlResponse searchSubscriber(String identityType, String userId, String site, String transactionId,
			boolean appendE164DigitPrefix) {

		log.debug("[{}] - Enter method searchSubscriber", transactionId);
		SearchRequest searchRequest = commonService.createSearchRequest(identityType, userId, site,
				appendE164DigitPrefix);
		return processSpmlRequest(searchRequest, transactionId);
	}

	DeleteRequest createDeleteRequest(String identifier) {
		return commonService.createDeleteRequest(identifier);
	}
}
