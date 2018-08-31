package com.charter.provisioning.hss.handler;

import com.charter.provisioning.hss.common.DigitalPhoneCommon;
import com.charter.provisioning.hss.common.MockObjectCreator;
import com.charter.provisioning.hss.config.HssServiceConfig;
import com.charter.provisioning.hss.exception.ServiceException;
import com.charter.provisioning.hss.model.DigitalPhoneResponse;
import com.charter.provisioning.hss.model.DigitalPhoneResponse.Status;
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
public class DeleteHGSubscriberHandlerTest {

	@InjectMocks
	private DeleteHGSubscriberHandler deleteHGSubscriber;

	@Mock
	private HssServiceConfig serviceConfig;

	@Mock
	private CommonSubscriberHandler commonHandler;

	@Mock
	private DigitalPhoneCommon digitalPhoneCommon;

	@Test
	public void execute_DeleteHGSubscriberForMultiplePublicIdentity_ExpectsNotFoundError() throws Exception {
		try {
			deleteHGSubscriber.execute(MockObjectCreator.getHGDigitalPhoneWithMultiplePublicIdentities(),
					MockObjectCreator.CORRELATION_ID);
		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_BAD_REQUEST));
		}
	}

	@Test
	public void execute_DeleteHGSubscriberForInValidMLhgIdPrefix_ExpectsBadRequestError() throws Exception {
		try {
			deleteHGSubscriber.execute(MockObjectCreator.getHGDigitalPhoneWithInvalidUserIdPrefix(),
					MockObjectCreator.CORRELATION_ID);

		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_BAD_REQUEST));
		}
	}

	@Test
	public void execute_DeleteHGSubscriberForInValidMLhgId_ExpectsBadRequestError() throws Exception {
		try {
			deleteHGSubscriber.execute(MockObjectCreator.getHGDigitalPhoneWithInvalidUserId(),
					MockObjectCreator.CORRELATION_ID);

		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_BAD_REQUEST));
		}
	}

	@Test
	public void execute_DeleteHGSubscriberForInValidMLhgIdLength_ExpectsBadRequestError() throws Exception {
		try {
			deleteHGSubscriber.execute(MockObjectCreator.getHGDigitalPhoneWithInvalidUserIdLength(),
					MockObjectCreator.CORRELATION_ID);

		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_BAD_REQUEST));
		}
	}

	@Test
	public void execute_DeleteHGSubscriberForNonExistingControllerId_ExpectsNotFoundError() throws Exception {

		try {
			when(serviceConfig.getOperationDelete()).thenReturn("delete");

			deleteHGSubscriber.execute(MockObjectCreator.getHGDigitalPhoneWithSinglePublicIdentity(),
					MockObjectCreator.CORRELATION_ID);
		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_NOT_FOUND));
		}
	}

	@Test
	public void execute_DeleteHGSubscriberForNonExistingTerminalId_ExpectsNotFoundError() throws Exception {

		try {
			when(serviceConfig.getOperationDelete()).thenReturn("delete");

			deleteHGSubscriber.execute(MockObjectCreator.getHGDigitalPhoneWithSingleTerminalPublicIdentity(),
					MockObjectCreator.CORRELATION_ID);
		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_NOT_FOUND));
		}
	}

	@Test
	public void execute_DeleteHGControllerSubscriberForValidInput_ExpectsSuccessDigitalPhoneResponse() {

		when(serviceConfig.getOperationDelete()).thenReturn("delete");
		when(commonHandler.searchSubscriberByNationalPublicIdentity(
				MockObjectCreator.getHGDigitalPhoneWithSinglePublicIdentity().getPublicIdentity().get(0),
				MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID))
						.thenReturn(MockObjectCreator.getHGSubscriber());

		when(commonHandler.createDeleteRequest(MockObjectCreator.SUBSCRIBER_ID))
				.thenReturn(MockObjectCreator.getDeleteRequest());
		when(commonHandler.processSpmlRequest(MockObjectCreator.getDeleteRequest(), MockObjectCreator.CORRELATION_ID))
				.thenReturn(MockObjectCreator.getSpmlResponse());
		when(commonHandler.createResponse(MockObjectCreator.getSpmlResponse()))
				.thenReturn(MockObjectCreator.getDigitalPhoneResponseWithSuccessStatus());

		DigitalPhoneResponse response = deleteHGSubscriber.execute(
				MockObjectCreator.getHGDigitalPhoneWithSinglePublicIdentity(), MockObjectCreator.CORRELATION_ID);
		assertThat(response.getStatus(), is(Status.SUCCESS));
	}

	@Test
	public void execute_DeleteHGTerminalSubscriberForValidInput_ExpectsSuccessDigitalPhoneResponse() {

		when(serviceConfig.getOperationDelete()).thenReturn("delete");
		when(commonHandler.searchSubscriberByNationalPublicIdentity(
				MockObjectCreator.getHGDigitalPhoneWithSingleTerminalPublicIdentity().getPublicIdentity().get(0),
				MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID))
						.thenReturn(MockObjectCreator.getHGSubscriber());

		when(digitalPhoneCommon
				.createModifyRequest(MockObjectCreator.getSubscriberWithMultiplePublicUserIds().getIdentifier()))
						.thenReturn(MockObjectCreator.getHGModifyRequestWithModification());

		when(serviceConfig.getOperationDelete()).thenReturn("delete");
		when(commonHandler.processSpmlRequest(MockObjectCreator.getHGModifyRequestWithModification(),
				MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getSpmlResponse());
		when(commonHandler.createResponse(MockObjectCreator.getSpmlResponse()))
				.thenReturn(MockObjectCreator.getDigitalPhoneResponseWithSuccessStatus());

		DigitalPhoneResponse response = deleteHGSubscriber.execute(
				MockObjectCreator.getHGDigitalPhoneWithSingleTerminalPublicIdentity(),
				MockObjectCreator.CORRELATION_ID);
		assertThat(response.getStatus(), is(Status.SUCCESS));
	}
}
