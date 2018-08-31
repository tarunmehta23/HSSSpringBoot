package com.charter.provisioning.hss.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.charter.provisioning.hss.factory.ServiceInterface;
import com.charter.provisioning.hss.model.DigitalPhone;
import com.charter.provisioning.hss.model.DigitalPhoneResponse;
import com.charter.provisioning.hss.model.DigitalPhoneResponse.Status;
import com.charter.provisioning.network.hss.subscriber.spml.AddRequest;
import com.charter.provisioning.network.hss.subscriber.spml.SpmlResponse;
import com.charter.provisioning.network.hss.subscriber.spml.schema.Subscriber;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CreateSubscriberHandler implements ServiceInterface {
	
	private CommonSubscriberHandler commonHandler;
	
	@Autowired
	public CreateSubscriberHandler(CommonSubscriberHandler commonHandler) {
		this.commonHandler = commonHandler;
	}

	/* Creates new HSS Subscriber.
	 * @see com.charter.provisioning.hss.factory.ServiceInterface#execute(com.charter.provisioning.hss.model.DigitalPhone, java.lang.String)
	 */
	@Override
	public DigitalPhoneResponse execute(DigitalPhone digitalPhone, String transactionId) {
		
		if (null != digitalPhone) {
		
			log.info("[{}] - Enter method CreateSubscriberHandler", transactionId);
			Subscriber subscriber;
	        
	        // Checking for subscriber with the first Public identity
	        subscriber = commonHandler.searchSubscriberByPublicIdentity(digitalPhone.getPublicIdentity().get(0), digitalPhone.getSite(), transactionId);
	       
	        if (subscriber == null) {
	            // Checking for subscriber with the first Private identity
	            subscriber = commonHandler.searchSubscriberByPrivateIdentity(digitalPhone.getPrivateIdentity().get(0), digitalPhone.getSite(), transactionId);
	        }
	        
	        // Functionality not supported now, dependent on SENETAD-628
	        if (subscriber != null) {
	      
	        }
	        
	        if (subscriber == null) {
	        	log.info("[{}] - Creating HSS subscriber ", transactionId);
	            return commonHandler.createResponse(createSubscriber(digitalPhone, transactionId));
	        } else {
	        	// Functionality not supported now. Not in the scope of Current Story.
	        	log.info("[{}] - Adding SecondaryLine to HSS subscriber ", transactionId);
	        }
		}
		return DigitalPhoneResponse.builder().status(Status.SUCCESS).build();
	}
	
	private SpmlResponse createSubscriber(DigitalPhone digitalPhone, String transactionId) {
		
		AddRequest addRequest = commonHandler.createAddRequest(digitalPhone);
		return commonHandler.processSpmlRequest(addRequest, transactionId);
	}

}
