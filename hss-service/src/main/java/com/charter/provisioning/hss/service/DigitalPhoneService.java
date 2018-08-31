package com.charter.provisioning.hss.service;

import com.charter.provisioning.hss.model.DigitalPhone;
import com.charter.provisioning.hss.model.DigitalPhoneResponse;
import com.charter.provisioning.network.hss.subscriber.spml.schema.Subscriber;

public interface DigitalPhoneService {
	
	DigitalPhoneResponse createSubscriber(DigitalPhone digitalPhone, String transactionId);
	
	DigitalPhoneResponse deleteSubscriber(DigitalPhone digitalPhone, String transactionId);

	Subscriber getDigitalPhoneSubscriber(String telephoneNumber, String controllerId, String privateIdentity, String transactionId);

}
