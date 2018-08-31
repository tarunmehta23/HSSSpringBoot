package com.charter.provisioning.hss.service;

import com.charter.provisioning.hss.common.MockObjectCreator;
import com.charter.provisioning.hss.config.HssServiceConfig;
import com.charter.provisioning.hss.exception.ServiceException;
import com.charter.provisioning.hss.factory.ServiceFactory;
import com.charter.provisioning.hss.factory.ServiceInterface;
import com.charter.provisioning.hss.handler.*;
import com.charter.provisioning.hss.model.DigitalPhoneResponse;
import com.charter.provisioning.hss.model.PrivateIdentity;
import com.charter.provisioning.hss.model.PublicIdentity;
import com.charter.provisioning.hss.model.DigitalPhoneResponse.Status;
import com.charter.provisioning.network.hss.subscriber.spml.schema.Subscriber;
import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DigitalPhoneServiceImplTest {

	@InjectMocks
	private DigitalPhoneServiceImpl digitalPhoneService;

	@Mock
	private CommonSubscriberHandler commonHandler;
	
	@Mock
	private CreateSubscriberHandler CreateSubscriberHandler;
	
	@Mock
	private CreateHGSubscriberHandler CreateHGSubscriberHandler;
	
	@Mock
	private CreateBGSubscriberHandler CreateBGSubscriberHandler;
	
	@Mock
	private DeleteSubscriberHandler DeleteSubscriberHandler;

	@Mock
	private DeleteHGSubscriberHandler DeleteHGSubscriberHandler;

	@Mock
	private DeleteBGSubscriberHandler DeleteBGSubscriberHandler;
	
	@Mock
	private HssServiceConfig serviceConfig;

	@Before
	public void setup() throws Exception {
		
		ReflectionTestUtils.setField(digitalPhoneService, "privateIdentityLength" , 16, int.class);
		
		List<ServiceInterface> list = new ArrayList<>();
		list.add(CreateSubscriberHandler);
		list.add(CreateHGSubscriberHandler);
		list.add(CreateBGSubscriberHandler);
		list.add(DeleteSubscriberHandler);
		list.add(DeleteHGSubscriberHandler);
		list.add(DeleteBGSubscriberHandler);

		ServiceFactory factory = new ServiceFactory(list);
		factory.initMyServiceCache();
	}

	@Test
	public void createSubscriber_testCreateSubscriberValidInput_ExpectsSuccess() throws Exception {
		
		when(serviceConfig.getDPhone()).thenReturn(MockObjectCreator.DPHONE);
		when(CreateSubscriberHandler.execute(MockObjectCreator.getDigitalPhone(),
				MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.createSuccessDigitalPhoneResponse());
		
		DigitalPhoneResponse response = digitalPhoneService.createSubscriber(MockObjectCreator.getDigitalPhone(), MockObjectCreator.CORRELATION_ID);
		assertThat(response.getStatus(), is(Status.CREATED));
	}
	
	@Test
	public void createSubscriber_testCreateHGSubscriberValidInput_ExpectsSuccess() throws Exception {

		when(serviceConfig.getDPhone()).thenReturn(MockObjectCreator.DPHONE);
		when(serviceConfig.getHGroup()).thenReturn(MockObjectCreator.HGROUP);
		when(CreateHGSubscriberHandler.execute(MockObjectCreator.getHGDigitalPhone(),
				MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.createSuccessDigitalPhoneResponse());
		
		DigitalPhoneResponse response = digitalPhoneService.createSubscriber(MockObjectCreator.getHGDigitalPhone(), MockObjectCreator.CORRELATION_ID);
		assertThat(response.getStatus(), is(Status.CREATED));
	}
	
	@Test
	public void createSubscriber_testCreateBGSubscriberValidInput_ExpectsSuccess() throws Exception {

		when(serviceConfig.getDPhone()).thenReturn(MockObjectCreator.DPHONE);
		when(serviceConfig.getHGroup()).thenReturn(MockObjectCreator.HGROUP);
		when(serviceConfig.getBGroup()).thenReturn(MockObjectCreator.BGROUP);
		when(CreateBGSubscriberHandler.execute(MockObjectCreator.getBGDigitalPhone(),
				MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.createSuccessDigitalPhoneResponse());
		
		DigitalPhoneResponse response = digitalPhoneService.createSubscriber(MockObjectCreator.getBGDigitalPhone(), MockObjectCreator.CORRELATION_ID);
		assertThat(response.getStatus(), is(Status.CREATED));
	}

	@Test
	public void deleteSubscriber_testDeleteSubscriberValidInput_ExpectsSuccess() throws Exception {

		when(serviceConfig.getDPhone()).thenReturn(MockObjectCreator.DPHONE);
		when(DeleteSubscriberHandler.execute(MockObjectCreator.getDigitalPhone(),
				MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getDigitalPhoneResponseWithSuccessStatus());

		DigitalPhoneResponse response = digitalPhoneService.deleteSubscriber(MockObjectCreator.getDigitalPhone(), MockObjectCreator.CORRELATION_ID);
		assertThat(response.getStatus(), is(Status.SUCCESS));
	}

	@Test
	public void deleteSubscriber_testDeleteHGSubscriberValidInput_ExpectsSuccess() throws Exception {

		when(serviceConfig.getDPhone()).thenReturn(MockObjectCreator.DPHONE);
		when(serviceConfig.getHGroup()).thenReturn(MockObjectCreator.HGROUP);
		when(DeleteHGSubscriberHandler.execute(MockObjectCreator.getHGDigitalPhone(),
				MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getDigitalPhoneResponseWithSuccessStatus());

		DigitalPhoneResponse response = digitalPhoneService.deleteSubscriber(MockObjectCreator.getHGDigitalPhone(), MockObjectCreator.CORRELATION_ID);
		assertThat(response.getStatus(), is(Status.SUCCESS));
	}

	@Test
	public void deleteSubscriber_testDeleteBGSubscriberValidInput_ExpectsSuccess() throws Exception {

		when(serviceConfig.getDPhone()).thenReturn(MockObjectCreator.DPHONE);
		when(serviceConfig.getHGroup()).thenReturn(MockObjectCreator.HGROUP);
		when(serviceConfig.getBGroup()).thenReturn(MockObjectCreator.BGROUP);
		when(DeleteBGSubscriberHandler.execute(MockObjectCreator.getBGDigitalPhone(),
				MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getDigitalPhoneResponseWithSuccessStatus());

		DigitalPhoneResponse response = digitalPhoneService.deleteSubscriber(MockObjectCreator.getBGDigitalPhone(), MockObjectCreator.CORRELATION_ID);
		assertThat(response.getStatus(), is(Status.SUCCESS));
	}

	@Test
	public void getDigitalPhoneSubscriber_retrieveHssSubscriberByTelephoneNumber_ExpectsSubscriberObject()
			throws Exception {

		when(commonHandler.searchSubscriberByPublicIdentity(
				PublicIdentity.builder().userId(MockObjectCreator.PHONE_NUMBER).build(), null,
				MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getSubscriber());

		Subscriber subscriber = digitalPhoneService.getDigitalPhoneSubscriber(MockObjectCreator.PHONE_NUMBER, null,
				null, MockObjectCreator.CORRELATION_ID);

		assertThat(subscriber.getIdentifier(), is(MockObjectCreator.SUBSCRIBER_ID));
	}

	@Test
	public void getDigitalPhoneSubscriber_retrieveHssSubscriberByNationalPublicIdentity_ExpectsSubscriberObject()
			throws Exception {

		when(commonHandler.searchSubscriberByNationalPublicIdentity(
				PublicIdentity.builder().userId(MockObjectCreator.NATIONAL_PUBLIC_IDENTITY).build(), null,
				MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getSubscriber());

		Subscriber subscriber = digitalPhoneService.getDigitalPhoneSubscriber(null,
				MockObjectCreator.NATIONAL_PUBLIC_IDENTITY, null, MockObjectCreator.CORRELATION_ID);

		assertThat(subscriber.getIdentifier(), is(MockObjectCreator.SUBSCRIBER_ID));
	}

	@Test
	public void getDigitalPhoneSubscriber_retrieveHssSubscriberByPrivateIdentity_ExpectsSubscriberObject()
			throws Exception {

		when(commonHandler.searchSubscriberByPrivateIdentity(
				PrivateIdentity.builder().userId(MockObjectCreator.PRIVATE_IDENTITY).build(), null,
				MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getSubscriber());

		Subscriber subscriber = digitalPhoneService.getDigitalPhoneSubscriber(null, null,
				MockObjectCreator.PRIVATE_IDENTITY, MockObjectCreator.CORRELATION_ID);

		assertThat(subscriber.getIdentifier(), is(MockObjectCreator.SUBSCRIBER_ID));
	}

	@Test
	public void getDigitalPhoneSubscriber_passNonExistingPublicIdentity_ExpectsError() throws Exception {
		try {
			when(commonHandler.searchSubscriberByPublicIdentity(
					PublicIdentity.builder().userId(MockObjectCreator.PHONE_NUMBER).build(), null,
					MockObjectCreator.CORRELATION_ID)).thenReturn(null);

			digitalPhoneService.getDigitalPhoneSubscriber(MockObjectCreator.PHONE_NUMBER, null, null,
					MockObjectCreator.CORRELATION_ID);
		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_NOT_FOUND));
		}
	}

	@Test
	public void getDigitalPhoneSubscriber_passNonExistingNationalPublicIdentity_ExpectsError() throws Exception {
		try {
			when(commonHandler.searchSubscriberByNationalPublicIdentity(
					PublicIdentity.builder().userId(MockObjectCreator.NATIONAL_PUBLIC_IDENTITY).build(), null,
					MockObjectCreator.CORRELATION_ID)).thenReturn(null);

			digitalPhoneService.getDigitalPhoneSubscriber(null, MockObjectCreator.NATIONAL_PUBLIC_IDENTITY, null,
					MockObjectCreator.CORRELATION_ID);
		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_NOT_FOUND));
		}
	}

	@Test
	public void getDigitalPhoneSubscriber_passNonExistingPrivateIdentity_ExpectsError() throws Exception {
		try {
			when(commonHandler.searchSubscriberByPrivateIdentity(
					PrivateIdentity.builder().userId(MockObjectCreator.PRIVATE_IDENTITY).build(), null,
					MockObjectCreator.CORRELATION_ID)).thenReturn(null);

			digitalPhoneService.getDigitalPhoneSubscriber(null, null, MockObjectCreator.PRIVATE_IDENTITY,
					MockObjectCreator.CORRELATION_ID);
		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_NOT_FOUND));
		}
	}

	@Test
	public void getDigitalPhoneSubscriber_passEmptyTelephoneNumber_ExpectsError() throws Exception {
		try {
			digitalPhoneService.getDigitalPhoneSubscriber(null, null, null, MockObjectCreator.CORRELATION_ID);
		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_BAD_REQUEST));
		}
	}

	@Test
	public void getDigitalPhoneSubscriber_passInvalidTelephoneNumber_ExpectsError() throws Exception {
		try {
			digitalPhoneService.getDigitalPhoneSubscriber("8216328&86", null, null, MockObjectCreator.CORRELATION_ID);
		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_BAD_REQUEST));
		}
	}

	@Test
	public void getDigitalPhoneSubscriber_passInvalidPickupGroupWithNonNumericCharacters_ExpectsError()
			throws Exception {
		try {
			digitalPhoneService.getDigitalPhoneSubscriber(null, "pickup_group_1a710", null,
					MockObjectCreator.CORRELATION_ID);
		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_BAD_REQUEST));
		}
	}

	@Test
	public void getDigitalPhoneSubscriber_passInvalidMLHGIdWithNonNumericCharacters_ExpectsError() throws Exception {
		try {
			digitalPhoneService.getDigitalPhoneSubscriber(null, "mlhg_5272@_0001", null,
					MockObjectCreator.CORRELATION_ID);
		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_BAD_REQUEST));
		}
	}

	@Test
	public void getDigitalPhoneSubscriber_passInvalidPrivateIdentityWithIncorrectLength_ExpectsError()
			throws Exception {
		try {
			digitalPhoneService.getDigitalPhoneSubscriber(null, null, "15FDD508E2FCC48139",
					MockObjectCreator.CORRELATION_ID);
		} catch (ServiceException ex) {
			assertThat(ex.getHttpStatus(), is(HttpStatus.SC_BAD_REQUEST));
		}
	}
}
