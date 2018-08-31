package com.charter.provisioning.hss.handler;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.apache.http.HttpStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.charter.provisioning.hss.common.MockObjectCreator;
import com.charter.provisioning.hss.exception.ServiceException;
import com.charter.provisioning.hss.model.DigitalPhoneResponse;

@RunWith(MockitoJUnitRunner.class)
public class DeleteBGSubscriberHandlerTest {

	@InjectMocks
	DeleteBGSubscriberHandler deleteBGSubscriber;

	@Mock
	private CommonSubscriberHandler commonHandler;
	
	@Test
	public void execute_DeleteBGSubscriberForInValidPickupGroupInput_ExpectsBadRequestError() throws Exception {
		try {
			deleteBGSubscriber.execute(MockObjectCreator.getDigitalPhone(), MockObjectCreator.CORRELATION_ID);
		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(org.apache.http.HttpStatus.SC_BAD_REQUEST));
		}
	}

	@Test
	public void execute_DeleteBGSubscriberForNonExistingPickupGroup_ExpectsNotFoundError() throws Exception {
		try {
			when(commonHandler.searchSubscriberByNationalPublicIdentity(
					MockObjectCreator.getBGDigitalPhone().getPublicIdentity().get(0), MockObjectCreator.SITE,
					MockObjectCreator.CORRELATION_ID)).thenReturn(null);

			deleteBGSubscriber.execute(MockObjectCreator.getBGDigitalPhone(), MockObjectCreator.CORRELATION_ID);
		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_NOT_FOUND));
		}
	}

	@Test
	public void execute_DeleteBGSubscriberWithValidInput_ExpectsSuccess() throws Exception {

		when(commonHandler.searchSubscriberByNationalPublicIdentity(
				MockObjectCreator.getBGDigitalPhone().getPublicIdentity().get(0), MockObjectCreator.SITE,
				MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getSubscriber());

		when(commonHandler.createDeleteRequest(MockObjectCreator.getSubscriber().getIdentifier()))
				.thenReturn(MockObjectCreator.getDeleteRequest());
		when(commonHandler.processSpmlRequest(MockObjectCreator.getDeleteRequest(), MockObjectCreator.CORRELATION_ID))
				.thenReturn(MockObjectCreator.getSpmlResponse());
		when(commonHandler.createResponse(MockObjectCreator.getSpmlResponse()))
				.thenReturn(MockObjectCreator.createSuccessDigitalPhoneResponse());

		DigitalPhoneResponse response = deleteBGSubscriber.execute(MockObjectCreator.getBGDigitalPhone(),
				MockObjectCreator.CORRELATION_ID);

		assertThat(response.getStatus(), is(DigitalPhoneResponse.Status.CREATED));
	}

}
