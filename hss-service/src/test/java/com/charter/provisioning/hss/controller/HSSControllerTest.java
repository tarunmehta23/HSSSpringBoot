package com.charter.provisioning.hss.controller;

import com.charter.provisioning.hss.common.MockObjectCreator;
import com.charter.provisioning.hss.exception.ErrorResponse;
import com.charter.provisioning.hss.exception.ServiceException;
import com.charter.provisioning.hss.model.DigitalPhoneResponse;
import com.charter.provisioning.hss.service.DigitalPhoneService;
import com.charter.provisioning.network.hss.subscriber.spml.schema.Subscriber;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HSSControllerTest {

	@InjectMocks
	private HSSController controller;

	@Mock
	private DigitalPhoneService digitalPhoneService;

	@Test
	public void createSubscriber_CreateSubscriberWithValidInput_ExpectsDigitalPhoneResponse() throws Exception {

		when(digitalPhoneService.createSubscriber(MockObjectCreator.createDigitalPhoneRequest(),
				MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.createSuccessDigitalPhoneResponse());

		ResponseEntity<DigitalPhoneResponse> responseEntity = controller
				.createSubscriber(MockObjectCreator.CORRELATION_ID, MockObjectCreator.createDigitalPhoneRequest());

		assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
		assertThat(responseEntity.getBody(), is(MockObjectCreator.createSuccessDigitalPhoneResponse()));
	}

	@Test
	public void deleteSubscriber_DeleteSubscriberWithValidInput_ExpectsDigitalPhoneResponse() throws Exception {

		when(digitalPhoneService.deleteSubscriber(MockObjectCreator.createDigitalPhoneRequest(),
				MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.createSuccessDigitalPhoneResponse());

		ResponseEntity<?> responseEntity = controller.deleteSubscriber(MockObjectCreator.PHONE_NUMBER,
				MockObjectCreator.CORRELATION_ID, MockObjectCreator.createDigitalPhoneRequest());

		assertThat(responseEntity.getStatusCode(), is(HttpStatus.NO_CONTENT));
	}

	@Test
	public void getDigitalPhone_getDigitalPhoneSubscriberWithTelephoneNumber_ExpectsSubscriberObject()
			throws Exception {

		when(digitalPhoneService.getDigitalPhoneSubscriber(MockObjectCreator.PHONE_NUMBER, null, null,
				MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getSubscriber());

		ResponseEntity<Subscriber> responseEntity = controller.getDigitalPhone(MockObjectCreator.CORRELATION_ID,
				MockObjectCreator.PHONE_NUMBER, null, null);

		assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
		assertThat(responseEntity.getBody(), is(MockObjectCreator.getSubscriber()));
	}

	@Test
	public void getDigitalPhone_getDigitalPhoneSubscriberWithControllerId_ExpectsSubscriberObject() throws Exception {

		when(digitalPhoneService.getDigitalPhoneSubscriber(null, MockObjectCreator.NATIONAL_PUBLIC_IDENTITY, null,
				MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getSubscriber());

		ResponseEntity<Subscriber> responseEntity = controller.getDigitalPhone(MockObjectCreator.CORRELATION_ID, null,
				MockObjectCreator.NATIONAL_PUBLIC_IDENTITY, null);

		assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
		assertThat(responseEntity.getBody(), is(MockObjectCreator.getSubscriber()));
	}

	@Test
	public void getDigitalPhone_getDigitalPhoneSubscriberWithPrivateIdentity_ExpectsSubscriberObject()
			throws Exception {

		when(digitalPhoneService.getDigitalPhoneSubscriber(null, null, MockObjectCreator.PRIVATE_IDENTITY,
				MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getSubscriber());

		ResponseEntity<Subscriber> responseEntity = controller.getDigitalPhone(MockObjectCreator.CORRELATION_ID, null,
				null, MockObjectCreator.PRIVATE_IDENTITY);

		assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
		assertThat(responseEntity.getBody(), is(MockObjectCreator.getSubscriber()));
	}

	@Test
	public void handleException_ServiceException_ExpectsErrorResponse() {

		MockHttpServletResponse response = new MockHttpServletResponse();
		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);

		String errorMessage = "Invalid site :DV8, Invalid phone number :821632888611.";
		ServiceException hcee = new ServiceException(HttpServletResponse.SC_BAD_REQUEST, errorMessage);

		ErrorResponse errorResponse = controller.handleException(response, hcee);

		assertThat(Integer.parseInt(errorResponse.getStatus()), is(HttpServletResponse.SC_BAD_REQUEST));
		assertThat((errorResponse).getMessage(), is(errorMessage));
	}
}
