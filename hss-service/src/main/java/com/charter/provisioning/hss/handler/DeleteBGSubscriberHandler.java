package com.charter.provisioning.hss.handler;

import com.charter.provisioning.hss.common.ServiceConstants;
import com.charter.provisioning.hss.exception.ServiceException;
import com.charter.provisioning.hss.factory.ServiceInterface;
import com.charter.provisioning.hss.model.DigitalPhone;
import com.charter.provisioning.hss.model.DigitalPhoneResponse;
import com.charter.provisioning.network.hss.subscriber.spml.DeleteRequest;
import com.charter.provisioning.network.hss.subscriber.spml.schema.Subscriber;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DeleteBGSubscriberHandler implements ServiceInterface {

	private CommonSubscriberHandler commonHandler;

	@Autowired
	public DeleteBGSubscriberHandler(CommonSubscriberHandler commonHandler) {
		super();
		this.commonHandler = commonHandler;
	}

	/*
	 * Deletes Existing HSS Subscriber
	 * @see com.charter.provisioning.hss.factory.ServiceInterface#execute(com.charter.provisioning.hss.model.DigitalPhone, java.lang.String)
	 */
	@Override
	public DigitalPhoneResponse execute(DigitalPhone digitalPhone, String transactionId) {

		if (null != digitalPhone) {
			log.info("Enter method DeleteBGSubscriberHandler");

			// Input validation check.
			validateBGPublicIdentity(digitalPhone, transactionId);
			
			// Checking for subscriber with the first Public identity
			Subscriber subscriber = commonHandler.searchSubscriberByNationalPublicIdentity(
					digitalPhone.getPublicIdentity().get(0), digitalPhone.getSite(), transactionId);
			if (subscriber != null) {
				DeleteRequest deleteRequest = commonHandler.createDeleteRequest(subscriber.getIdentifier());
				return commonHandler.createResponse(commonHandler.processSpmlRequest(deleteRequest, transactionId));
			} else {
				log.error("could not found subscriber for passed in pickupGroup :{}",
						digitalPhone.getPublicIdentity().get(0).getUserId());
				throw new ServiceException(HttpStatus.NOT_FOUND.value(), "Could not found subscriber");
			}
		}
		return DigitalPhoneResponse.builder().status(DigitalPhoneResponse.Status.FAILURE).build();
	}

	private void validateBGPublicIdentity(DigitalPhone digitalPhone, String transactionId) {

		log.debug("[{}] - Validating BG public identity.", transactionId);
		if (digitalPhone.getPublicIdentity().size() > 1) {
			log.error("[{}] - Multiple Public identities found in request.", transactionId);
			throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "Multiple Public identities found in request.");
		}

		String pickupGroup = digitalPhone.getPublicIdentity().get(0).getUserId();
		if (!(pickupGroup.startsWith(ServiceConstants.PICKUP_GROUP)
				&& StringUtils.isNumeric(pickupGroup.replace(ServiceConstants.PICKUP_GROUP, "")))) {

			log.error("[{}] - Invalid pickup group passed :{}", transactionId, pickupGroup);
			throw new ServiceException(HttpStatus.BAD_REQUEST.value(), "Invalid pickup group passed.");
		}
	}
}
