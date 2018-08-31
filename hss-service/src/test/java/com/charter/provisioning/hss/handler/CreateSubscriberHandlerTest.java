package com.charter.provisioning.hss.handler;

import com.charter.provisioning.hss.common.MockObjectCreator;
import com.charter.provisioning.hss.model.DigitalPhoneResponse;
import com.charter.provisioning.hss.model.DigitalPhoneResponse.Status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CreateSubscriberHandlerTest {

	@InjectMocks
	private CreateSubscriberHandler createSubscriberHandler;
	
	@Mock
	private CommonSubscriberHandler commonHandler;
	
	@Test
	public void execute_CreateSubscriberForExistingPublicIdentity_ExpectsSuccessDigitalPhoneResponse() throws Exception {
			
		when(commonHandler.searchSubscriberByPublicIdentity(MockObjectCreator.getPublicIdentitys().get(0),
				MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getSubscriber());
		
		DigitalPhoneResponse response = createSubscriberHandler.execute(MockObjectCreator.getDigitalPhone(), MockObjectCreator.CORRELATION_ID);
		assertThat(response.getStatus(), is(Status.SUCCESS));
	}
	
	@Test
	public void execute_CreateSubscriberForExistingPrivateIdentity_ExpectsSuccessDigitalPhoneResponse() throws Exception {
			
		when(commonHandler.searchSubscriberByPublicIdentity(MockObjectCreator.getPublicIdentitys().get(0),
				MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID)).thenReturn(null);
		when(commonHandler.searchSubscriberByPrivateIdentity(MockObjectCreator.getPrivateIdentitys().get(0),
				MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getSubscriber());
		
		DigitalPhoneResponse response = createSubscriberHandler.execute(MockObjectCreator.getDigitalPhone(), MockObjectCreator.CORRELATION_ID);
		assertThat(response.getStatus(), is(Status.SUCCESS));
	}
	
	@Test
	public void execute_CreateSubscriberWithValidInput_ExpectsSuccessDigitalPhoneResponse() throws Exception {
			
		when(commonHandler.searchSubscriberByPublicIdentity(MockObjectCreator.getPublicIdentitys().get(0),
				MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID)).thenReturn(null);
		when(commonHandler.searchSubscriberByPrivateIdentity(MockObjectCreator.getPrivateIdentitys().get(0),
				MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID)).thenReturn(null);
		when(commonHandler.createAddRequest(MockObjectCreator.getDigitalPhone())).thenReturn(MockObjectCreator.getAddRequest());
		when(commonHandler.processSpmlRequest(MockObjectCreator.getAddRequest(), MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getSpmlResponse());
		when(commonHandler.createResponse(MockObjectCreator.getSpmlResponse())).thenReturn(MockObjectCreator.getDigitalPhoneResponseWithSuccessStatus());
		
		DigitalPhoneResponse response = createSubscriberHandler.execute(MockObjectCreator.getDigitalPhone(), MockObjectCreator.CORRELATION_ID);
		assertThat(response.getStatus(), is(Status.SUCCESS));
	}
	
	@Test
	public void execute_CreateSubscriberWithValidInput_ExpectsFailedDigitalPhoneResponse() throws Exception {
			
		when(commonHandler.searchSubscriberByPublicIdentity(MockObjectCreator.getPublicIdentitys().get(0),
				MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID)).thenReturn(null);
		when(commonHandler.searchSubscriberByPrivateIdentity(MockObjectCreator.getPrivateIdentitys().get(0),
				MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID)).thenReturn(null);
		when(commonHandler.createAddRequest(MockObjectCreator.getDigitalPhone())).thenReturn(MockObjectCreator.getAddRequest());
		when(commonHandler.processSpmlRequest(MockObjectCreator.getAddRequest(), MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getFailedSpmlResponse());
		when(commonHandler.createResponse(MockObjectCreator.getFailedSpmlResponse())).thenReturn(MockObjectCreator.createFailedDigitalPhoneResponse());
		
		DigitalPhoneResponse response = createSubscriberHandler.execute(MockObjectCreator.getDigitalPhone(), MockObjectCreator.CORRELATION_ID);
		assertThat(response.getStatus(), is(Status.FAILURE));
	}
}
