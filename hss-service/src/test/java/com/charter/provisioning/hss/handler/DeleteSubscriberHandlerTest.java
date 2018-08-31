package com.charter.provisioning.hss.handler;

import com.charter.provisioning.hss.common.DigitalPhoneCommon;
import com.charter.provisioning.hss.common.MockObjectCreator;
import com.charter.provisioning.hss.config.HssServiceConfig;
import com.charter.provisioning.hss.exception.ServiceException;
import com.charter.provisioning.hss.model.DigitalPhoneResponse;
import com.charter.provisioning.hss.model.DigitalPhoneResponse.Status;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DeleteSubscriberHandlerTest {
	
	@InjectMocks
	private DeleteSubscriberHandler deleteSubscriber;
	
	@Mock
	private HssServiceConfig serviceConfig;
	
	@Mock
	private CommonSubscriberHandler commonHandler;

	@Mock
	private DigitalPhoneCommon digitalPhoneCommon;

	@Test(expected = ServiceException.class)
	public void execute_DeleteSubscriberForNoExistingPublicIdentity_ExpectsServiceException() throws Exception {
		when(commonHandler.searchSubscriberByPublicIdentity(MockObjectCreator.getPublicIdentitys().get(0),
				MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID)).thenReturn(null);
		
		deleteSubscriber.execute(MockObjectCreator.getDigitalPhone(), MockObjectCreator.CORRELATION_ID);
	}

	@Test
	public void execute_DeleteSubscriberForNoExistingPublicIdentity_ExpectsNotFoundStatus() throws Exception {
		try {
			when(commonHandler.searchSubscriberByPublicIdentity(MockObjectCreator.getPublicIdentitys().get(0),
					MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID)).thenReturn(null);

			deleteSubscriber.execute(MockObjectCreator.getDigitalPhone(), MockObjectCreator.CORRELATION_ID);
			fail("test case should throw a ServiceException.");

		} catch (ServiceException e) {
			assertThat(e.getHttpStatus(), is(HttpStatus.NOT_FOUND.value()));
		}
	}

	@Test
	public void execute_DeleteSubscriberForValidInput_ExpectsSuccessDigitalPhoneResponse() {
		
		when(commonHandler.searchSubscriberByPublicIdentity(MockObjectCreator.getPublicIdentitysForDelete().get(0),
				MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getSubscriber());
		
		when(serviceConfig.getOperationDelete()).thenReturn("delete");
		when(serviceConfig.getSipPrefix()).thenReturn("sip:");
		when(serviceConfig.getE164DigitPrefix()).thenReturn("+1");
		
		when(commonHandler.createDeleteRequest(MockObjectCreator.SUBSCRIBER_ID)).thenReturn(MockObjectCreator.getDeleteRequest());
		when(commonHandler.processSpmlRequest(MockObjectCreator.getDeleteRequest(), MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getSpmlResponse());
		
		when(commonHandler.createResponse(MockObjectCreator.getSpmlResponse())).thenReturn(MockObjectCreator.getDigitalPhoneResponseWithSuccessStatus());
		
		DigitalPhoneResponse response = deleteSubscriber.execute(MockObjectCreator.getDigitalPhoneDeleteRequest(), MockObjectCreator.CORRELATION_ID);
		assertThat(response.getStatus(), is(Status.SUCCESS));
	}
	
	@Test(expected = ServiceException.class)
	public void execute_DeleteSubscriberForInValidInput_ExpectsServiceException() {
		
		when(commonHandler.searchSubscriberByPublicIdentity(MockObjectCreator.getPublicIdentitys().get(0),
				MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getSubscriber());
		
		when(serviceConfig.getOperationDelete()).thenReturn("delete");
		
		DigitalPhoneResponse response = deleteSubscriber.execute(MockObjectCreator.getDigitalPhone(), MockObjectCreator.CORRELATION_ID);
		assertThat(response.getStatus(), is(Status.SUCCESS));
	}
	
	@Test
	public void execute_DeleteSubscriberForInValidSubscriber_ExpectsSuccessDigitalPhoneResponse() {
		
		when(commonHandler.searchSubscriberByPublicIdentity(MockObjectCreator.getPublicIdentitysForDelete().get(0),
				MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getInvalidSubscriber());
		
		when(serviceConfig.getOperationDelete()).thenReturn("delete");
		
		when(commonHandler.createDeleteRequest(MockObjectCreator.SUBSCRIBER_ID)).thenReturn(MockObjectCreator.getDeleteRequest());
		when(commonHandler.processSpmlRequest(MockObjectCreator.getDeleteRequest(), MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getSpmlResponse());
		
		when(commonHandler.createResponse(MockObjectCreator.getSpmlResponse())).thenReturn(MockObjectCreator.getDigitalPhoneResponseWithSuccessStatus());
		
		DigitalPhoneResponse response = deleteSubscriber.execute(MockObjectCreator.getDigitalPhoneDeleteRequest(), MockObjectCreator.CORRELATION_ID);
		assertThat(response.getStatus(), is(Status.SUCCESS));
	}
	
	@Test
	public void execute_DeleteSubscriberForNullSubscriberPublicId_ExpectsSuccessDigitalPhoneResponse() {
		
		when(commonHandler.searchSubscriberByPublicIdentity(MockObjectCreator.getPublicIdentitysForDelete().get(0),
				MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getInvalidSubscriberWithNullPublicId());
		
		when(serviceConfig.getOperationDelete()).thenReturn("delete");
		
		when(commonHandler.createDeleteRequest(MockObjectCreator.SUBSCRIBER_ID)).thenReturn(MockObjectCreator.getDeleteRequest());
		when(commonHandler.processSpmlRequest(MockObjectCreator.getDeleteRequest(), MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getSpmlResponse());
		
		when(commonHandler.createResponse(MockObjectCreator.getSpmlResponse())).thenReturn(MockObjectCreator.getDigitalPhoneResponseWithSuccessStatus());
		
		DigitalPhoneResponse response = deleteSubscriber.execute(MockObjectCreator.getDigitalPhoneDeleteRequest(), MockObjectCreator.CORRELATION_ID);
		assertThat(response.getStatus(), is(Status.SUCCESS));
	}
	
	@Test
	public void execute_DeleteSubscriberForEmptySubscriberPublicId_ExpectsSuccessDigitalPhoneResponse() {
		
		when(commonHandler.searchSubscriberByPublicIdentity(MockObjectCreator.getPublicIdentitysForDelete().get(0),
				MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getInvalidSubscriberWithEmptyPublicId());
		
		when(serviceConfig.getOperationDelete()).thenReturn("delete");
		
		when(commonHandler.createDeleteRequest(MockObjectCreator.SUBSCRIBER_ID)).thenReturn(MockObjectCreator.getDeleteRequest());
		when(commonHandler.processSpmlRequest(MockObjectCreator.getDeleteRequest(), MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getSpmlResponse());
		
		when(commonHandler.createResponse(MockObjectCreator.getSpmlResponse())).thenReturn(MockObjectCreator.getDigitalPhoneResponseWithSuccessStatus());
		
		DigitalPhoneResponse response = deleteSubscriber.execute(MockObjectCreator.getDigitalPhoneDeleteRequest(), MockObjectCreator.CORRELATION_ID);
		assertThat(response.getStatus(), is(Status.SUCCESS));
	}
	
	@Test
	public void execute_DeleteSubscriberForSecondaryLine_ExpectsSuccessDigitalPhoneResponse() {

		try {
			when(commonHandler.searchSubscriberByPublicIdentity(MockObjectCreator.getPublicIdentitysForDelete().get(0),
					MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID))
							.thenReturn(MockObjectCreator.getSubscriberWithMultiplePublicUserIds());

			when(serviceConfig.getOperationDelete()).thenReturn("delete");
			when(serviceConfig.getSipPrefix()).thenReturn("sip:");
			when(serviceConfig.getE164DigitPrefix()).thenReturn("+1");

			when(digitalPhoneCommon
					.createModifyRequest(MockObjectCreator.getSubscriberWithMultiplePublicUserIds().getIdentifier()))
							.thenReturn(MockObjectCreator.getHGModifyRequestWithModification());

			when(commonHandler.processSpmlRequest(MockObjectCreator.getModifyRequestWithModification(),
					MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getSpmlResponse());
			when(commonHandler.createResponse(MockObjectCreator.getSpmlResponse()))
					.thenReturn(MockObjectCreator.getDigitalPhoneResponseWithSuccessStatus());

			DigitalPhoneResponse response = deleteSubscriber.execute(MockObjectCreator.getDigitalPhoneDeleteRequest(),
					MockObjectCreator.CORRELATION_ID);
			assertThat(response.getStatus(), is(Status.SUCCESS));
		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.BAD_REQUEST));
		}
	}
}
