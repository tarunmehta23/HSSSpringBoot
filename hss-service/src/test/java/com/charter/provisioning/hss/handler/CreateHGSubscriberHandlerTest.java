package com.charter.provisioning.hss.handler;

import com.charter.provisioning.hss.common.DigitalPhoneCommon;
import com.charter.provisioning.hss.common.MockObjectCreator;
import com.charter.provisioning.hss.config.HssServiceConfig;
import com.charter.provisioning.hss.exception.ServiceException;
import com.charter.provisioning.hss.model.DigitalPhoneResponse;
import com.charter.provisioning.hss.model.PublicIdentity;
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
public class CreateHGSubscriberHandlerTest {

	@InjectMocks
	private CreateHGSubscriberHandler createHGSubscriberHandler;

	@Mock
	private CommonSubscriberHandler commonHandler;

	@Mock
	private DigitalPhoneCommon digitalPhoneCommon;
	
	@Mock
	private HssServiceConfig serviceConfig;

	@Test
	public void execute_CreateHGSubscriberForNonExistingPublicIdentity_ExpectsNotFoundError() throws Exception {
		try {
			createHGSubscriberHandler.execute(MockObjectCreator.getDigitalPhone(), MockObjectCreator.CORRELATION_ID);
		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_NOT_FOUND));
		}
	}

	@Test
	public void execute_CreateHGSubscriberForInValidMLhgIdInput_ExpectsBadRequestError() throws Exception {

		try {
			when(commonHandler.searchSubscriberByPublicIdentity(MockObjectCreator.getPublicIdentitys().get(0),
					MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID))
							.thenReturn(MockObjectCreator.getSubscriber());
			createHGSubscriberHandler.execute(MockObjectCreator.getDigitalPhone(), MockObjectCreator.CORRELATION_ID);

		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_BAD_REQUEST));
		}
	}

	@Test
	public void execute_CreateHGSubscriberForExistingMLhgId_ExpectsBadRequestError() throws Exception {

		try {
			when(commonHandler.searchSubscriberByPublicIdentity(MockObjectCreator.getHGPublicIdentityList().get(0),
					MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID))
							.thenReturn(MockObjectCreator.getSubscriber());
			when(commonHandler.searchSubscriberByNationalPublicIdentity(
					PublicIdentity.builder().operation("create").userId("mlhg_409077_0000").build(),
					MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID))
							.thenReturn(MockObjectCreator.getSubscriber());
			createHGSubscriberHandler.execute(MockObjectCreator.getHGDigitalPhone(), MockObjectCreator.CORRELATION_ID);
		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_BAD_REQUEST));
		}
	}

	@Test
	public void execute_CreateHGSubscriberWithValidInputButFailsToCreateHGController_ExpectsInternalServerError()
			throws Exception {

		try {
			when(commonHandler.searchSubscriberByPublicIdentity(MockObjectCreator.getHGPublicIdentityList().get(0),
					MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID))
							.thenReturn(MockObjectCreator.getSubscriber());
			when(commonHandler.searchSubscriberByNationalPublicIdentity(
					PublicIdentity.builder().operation("create").userId("mlhg_409077_0000").build(),
					MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID)).thenReturn(null);
			when(serviceConfig.getHGroup()).thenReturn(MockObjectCreator.HGROUP);
			when(digitalPhoneCommon.getFeaturesByName(MockObjectCreator.HGROUP))
					.thenReturn(MockObjectCreator.createHGFeatures());
			when(serviceConfig.getOperationCreate()).thenReturn(MockObjectCreator.OPERATION_CREATE);
			when(serviceConfig.getOperationUpdate()).thenReturn(MockObjectCreator.OPERATION_UPDATE);
			when(digitalPhoneCommon.generate16CharRandomKey()).thenReturn(MockObjectCreator.PRIVATE_IDENTITY);
			when(digitalPhoneCommon.generate32CharTimestampRandomKey()).thenReturn("01623E63117EB61561AD55AE4C072CA4");
			when(commonHandler.processSpmlRequest(null, MockObjectCreator.CORRELATION_ID))
					.thenReturn(MockObjectCreator.getFailedSpmlResponse());
			createHGSubscriberHandler.execute(MockObjectCreator.getHGDigitalPhone(), MockObjectCreator.CORRELATION_ID);

		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_INTERNAL_SERVER_ERROR));
		}
	}

	@Test
	public void execute_CreateHGSubscriberWithValidInput_ExpectsSuccess() throws Exception {

		when(commonHandler.searchSubscriberByPublicIdentity(MockObjectCreator.getHGPublicIdentityList().get(0),
				MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID))
						.thenReturn(MockObjectCreator.getHGSubscriber());
		when(commonHandler.searchSubscriberByNationalPublicIdentity(
				PublicIdentity.builder().operation("create").userId("mlhg_409077_0000").build(), MockObjectCreator.SITE,
				MockObjectCreator.CORRELATION_ID)).thenReturn(null);
		when(serviceConfig.getHGroup()).thenReturn(MockObjectCreator.HGROUP);
		when(digitalPhoneCommon.getFeaturesByName(MockObjectCreator.HGROUP))
				.thenReturn(MockObjectCreator.createHGFeatures());
		when(digitalPhoneCommon.generate16CharRandomKey()).thenReturn(MockObjectCreator.PRIVATE_IDENTITY);
		when(digitalPhoneCommon.generate32CharTimestampRandomKey()).thenReturn("01623E63117EB61561AD55AE4C072CA4");
		when(serviceConfig.getOperationCreate()).thenReturn(MockObjectCreator.OPERATION_CREATE);
		when(serviceConfig.getOperationUpdate()).thenReturn(MockObjectCreator.OPERATION_UPDATE);
		when(commonHandler.processSpmlRequest(null, MockObjectCreator.CORRELATION_ID))
				.thenReturn(MockObjectCreator.getSpmlResponse());
		when(digitalPhoneCommon.createModifyRequest(MockObjectCreator.SUBSCRIBER_ID))
				.thenReturn(MockObjectCreator.getHGModifyRequest());
		when(commonHandler.createResponse(null)).thenReturn(MockObjectCreator.createSuccessDigitalPhoneResponse());

		DigitalPhoneResponse response = createHGSubscriberHandler.execute(MockObjectCreator.getHGDigitalPhone(),
				MockObjectCreator.CORRELATION_ID);
		assertThat(response.getStatus(), is(DigitalPhoneResponse.Status.CREATED));
	}
}
