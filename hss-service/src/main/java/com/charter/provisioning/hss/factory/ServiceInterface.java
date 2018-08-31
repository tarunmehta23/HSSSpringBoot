package com.charter.provisioning.hss.factory;

import com.charter.provisioning.hss.model.DigitalPhone;
import com.charter.provisioning.hss.model.DigitalPhoneResponse;

public interface ServiceInterface {

	DigitalPhoneResponse execute(DigitalPhone digitalPhone, String transactionId); 
}
