package com.charter.provisioning.hss.handler;

import com.charter.provisioning.hss.common.DigitalPhoneCommon;
import com.charter.provisioning.hss.common.MockObjectCreator;
import com.charter.provisioning.hss.config.HssServiceConfig;
import com.charter.provisioning.hss.exception.ServiceException;
import com.charter.provisioning.hss.model.DigitalPhoneResponse;
import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CreateBGSubscriberHandlerTest {
	
	@InjectMocks
	private CreateBGSubscriberHandler createBGSubscriberHandler;

	@Mock
	private CommonSubscriberHandler commonHandler;

	@Mock
	private DigitalPhoneCommon digitalPhoneCommon;
	
	@Mock
	private HssServiceConfig serviceConfig;
	
	@Test
	public void execute_CreateBGSubscriberForInValidPickupGroupInput_ExpectsBadRequestError() throws Exception {
		try {
			createBGSubscriberHandler.execute(MockObjectCreator.getDigitalPhone(), MockObjectCreator.CORRELATION_ID);
		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_BAD_REQUEST));
		}
	}
	
	@Test
	public void execute_CreateBGSubscriberForExistingPickupGroup_ExpectsBadRequestError() throws Exception {
		try {
			when(commonHandler.searchSubscriberByNationalPublicIdentity(MockObjectCreator.getBGDigitalPhone().getPublicIdentity().get(0),
					MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID))
							.thenReturn(MockObjectCreator.getSubscriber());
			
			createBGSubscriberHandler.execute(MockObjectCreator.getBGDigitalPhone(), MockObjectCreator.CORRELATION_ID);
		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_BAD_REQUEST));
		}
	}
	
	@Test
	public void execute_CreateBGSubscriberWithValidInput_ExpectsSuccess() throws Exception {

		when(commonHandler.searchSubscriberByNationalPublicIdentity(MockObjectCreator.getBGDigitalPhone().getPublicIdentity().get(0),
				MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID))
						.thenReturn(null);
		when(digitalPhoneCommon.generate16CharRandomKey()).thenReturn(MockObjectCreator.PRIVATE_IDENTITY);
		when(digitalPhoneCommon.generate32CharTimestampRandomKey()).thenReturn("01623E63117EB61561AD55AE4C072CA4");
		when(serviceConfig.getOperationCreate()).thenReturn(MockObjectCreator.OPERATION_CREATE);
		when(commonHandler.processSpmlRequest(null, MockObjectCreator.CORRELATION_ID))
				.thenReturn(MockObjectCreator.getSpmlResponse());
		when(commonHandler.createResponse(MockObjectCreator.getSpmlResponse())).thenReturn(MockObjectCreator.createSuccessDigitalPhoneResponse());

		DigitalPhoneResponse response = createBGSubscriberHandler.execute(MockObjectCreator.getBGDigitalPhone(),
				MockObjectCreator.CORRELATION_ID);
		
		assertThat(response.getStatus(), is(DigitalPhoneResponse.Status.CREATED));
	}
}
