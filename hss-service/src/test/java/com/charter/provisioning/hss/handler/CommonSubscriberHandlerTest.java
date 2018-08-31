package com.charter.provisioning.hss.handler;

import com.charter.provisioning.hss.common.DigitalPhoneCommon;
import com.charter.provisioning.hss.common.MockObjectCreator;
import com.charter.provisioning.hss.common.SoapMessage;
import com.charter.provisioning.hss.config.SerializableConfig;
import com.charter.provisioning.hss.config.SpmlConfig;
import com.charter.provisioning.hss.external.HSSSubscriberProxy;
import com.charter.provisioning.hss.model.DigitalPhoneResponse;
import com.charter.provisioning.hss.model.DigitalPhoneResponse.Status;
import com.charter.provisioning.network.hss.subscriber.spml.AddRequest;
import com.charter.provisioning.network.hss.subscriber.spml.DeleteRequest;
import com.charter.provisioning.network.hss.subscriber.spml.SpmlResponse;
import com.charter.provisioning.network.hss.subscriber.spml.schema.Subscriber;
import org.hamcrest.core.IsNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CommonSubscriberHandlerTest {

	@InjectMocks
	private CommonSubscriberHandler commonSubscriberHandler;
	
	@Mock
	private SoapMessage soapMessage;
	
	@Mock
	private SpmlConfig spmlConfig;
	
	@Mock
	private DigitalPhoneCommon commonService;
	
	@Mock
	private SerializableConfig serializableConfig;
	
	@Mock
	private HSSSubscriberProxy subscriberProxy;
	
	@Test
	public void searchSubscriberByPublicIdentity_SearchForExistingPublicIdentity_ExpectsValidSubscriber() throws Exception {
			
		when(commonService.populateSoapMessageProperties(soapMessage)).thenReturn(soapMessage);
		when(subscriberProxy.sendAndReceive(soapMessage, MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getStringSearchResponse());
		when(serializableConfig.unmarshall(MockObjectCreator.getStringSearchResponse())).thenReturn(MockObjectCreator.getSpmlResponse());
		
		commonSubscriberHandler.searchSubscriber(MockObjectCreator.PUBLIC_SEARCH_NAME, MockObjectCreator.PHONE_NUMBER,
				MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID, false);
		
		Subscriber subscriber = commonSubscriberHandler.searchSubscriberByPublicIdentity(MockObjectCreator.getPublicIdentitys().get(0), MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID);
		assertThat(subscriber.getIdentifier(), is("C61561BD55AE4C072C92"));
	}
	
	@Test
	public void searchSubscriberByPublicIdentity_SearchForEmptyPublicIdentity_ExpectsNullSubscriber() throws Exception {
		
		Subscriber subscriber = commonSubscriberHandler.searchSubscriberByPublicIdentity(null, MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID);
		assertThat(subscriber, is(IsNull.nullValue()));
	}
	
	@Test
	public void searchSubscriberByPublicIdentity_SearchPublicIdentityWithNoSubscriber_ExpectsNullSubscriber() throws Exception {
		
		when(commonService.populateSoapMessageProperties(soapMessage)).thenReturn(soapMessage);		
		when(subscriberProxy.sendAndReceive(soapMessage, MockObjectCreator.CORRELATION_ID)).thenReturn("");

		Subscriber subscriber = commonSubscriberHandler.searchSubscriberByPublicIdentity(MockObjectCreator.getPublicIdentitys().get(0), MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID);
		assertThat(subscriber, is(IsNull.nullValue()));
	}
	
	@Test
	public void searchSubscriberByPublicIdentity_SearchForPublicIdentityWithFailedResponse_ExpectsNullSubscriber() throws Exception {
		
		when(commonService.populateSoapMessageProperties(soapMessage)).thenReturn(soapMessage);		
		when(subscriberProxy.sendAndReceive(soapMessage, MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getStringSearchResponse());
		when(serializableConfig.unmarshall(MockObjectCreator.getStringSearchResponse())).thenReturn(MockObjectCreator.getFailedSpmlResponse());

		Subscriber subscriber = commonSubscriberHandler.searchSubscriberByPublicIdentity(MockObjectCreator.getPublicIdentitys().get(0), MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID);
		assertThat(subscriber, is(IsNull.nullValue()));
	}

	@Test
	public void searchSubscriberByNationalPublicIdentity_SearchForExistingPublicIdentity_ExpectsValidSubscriber() throws Exception {

		when(commonService.populateSoapMessageProperties(soapMessage)).thenReturn(soapMessage);
		when(subscriberProxy.sendAndReceive(soapMessage, MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getStringSearchResponse());
		when(serializableConfig.unmarshall(MockObjectCreator.getStringSearchResponse())).thenReturn(MockObjectCreator.getSpmlResponse());

		commonSubscriberHandler.searchSubscriber(MockObjectCreator.PUBLIC_SEARCH_NAME, MockObjectCreator.NATIONAL_PUBLIC_IDENTITY,
				MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID, false);

		Subscriber subscriber = commonSubscriberHandler.searchSubscriberByNationalPublicIdentity(MockObjectCreator.getPublicIdentitys().get(0), MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID);
		assertThat(subscriber.getIdentifier(), is("C61561BD55AE4C072C92"));
	}

	@Test
	public void searchSubscriberByNationalPublicIdentity_SearchForEmptyPublicIdentity_ExpectsNullSubscriber() throws Exception {

		Subscriber subscriber = commonSubscriberHandler.searchSubscriberByNationalPublicIdentity(null, MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID);
		assertThat(subscriber, is(IsNull.nullValue()));
	}

	@Test
	public void searchSubscriberByNationalPublicIdentity_SearchPublicIdentityWithNoSubscriber_ExpectsNullSubscriber() throws Exception {

		when(commonService.populateSoapMessageProperties(soapMessage)).thenReturn(soapMessage);
		when(subscriberProxy.sendAndReceive(soapMessage, MockObjectCreator.CORRELATION_ID)).thenReturn("");

		Subscriber subscriber = commonSubscriberHandler.searchSubscriberByNationalPublicIdentity(MockObjectCreator.getPublicIdentitys().get(0), MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID);
		assertThat(subscriber, is(IsNull.nullValue()));
	}

	@Test
	public void searchSubscriberByNationalPublicIdentity_SearchForPublicIdentityWithFailedResponse_ExpectsNullSubscriber() throws Exception {

		when(commonService.populateSoapMessageProperties(soapMessage)).thenReturn(soapMessage);
		when(subscriberProxy.sendAndReceive(soapMessage, MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getStringSearchResponse());
		when(serializableConfig.unmarshall(MockObjectCreator.getStringSearchResponse())).thenReturn(MockObjectCreator.getFailedSpmlResponse());

		Subscriber subscriber = commonSubscriberHandler.searchSubscriberByNationalPublicIdentity(MockObjectCreator.getPublicIdentitys().get(0), MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID);
		assertThat(subscriber, is(IsNull.nullValue()));
	}
	
	@Test
	public void searchSubscriberByPrivateIdentity_SearchForExistingPrivateIdentity_ExpectsValidSubscriber() throws Exception {
		
		when(commonService.populateSoapMessageProperties(soapMessage)).thenReturn(soapMessage);
		when(subscriberProxy.sendAndReceive(soapMessage, MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getStringSearchResponse());
		when(serializableConfig.unmarshall(MockObjectCreator.getStringSearchResponse())).thenReturn(MockObjectCreator.getSpmlResponse());
		
		Subscriber subscriber = commonSubscriberHandler.searchSubscriberByPrivateIdentity(MockObjectCreator.getPrivateIdentitys().get(0), MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID);
		assertThat(subscriber.getIdentifier(), is("C61561BD55AE4C072C92"));
	}
	
	@Test
	public void searchSubscriberByPrivateIdentity_SearchForEmptyPrivateIdentity_ExpectsNullSubscriber() throws Exception {
			
		Subscriber subscriber = commonSubscriberHandler.searchSubscriberByPrivateIdentity(null, MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID);		
		assertThat(subscriber, is(IsNull.nullValue()));
	}
	
	@Test
	public void searchSubscriberByPrivateIdentity_SearchForPrivateIdentityWithFailedResponse_ExpectsNullSubscriber() throws Exception {
		
		when(commonService.populateSoapMessageProperties(soapMessage)).thenReturn(soapMessage);		
		when(subscriberProxy.sendAndReceive(soapMessage, MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getStringSearchResponse());
		when(serializableConfig.unmarshall(MockObjectCreator.getStringSearchResponse())).thenReturn(MockObjectCreator.getFailedSpmlResponse());

		Subscriber subscriber = commonSubscriberHandler.searchSubscriberByPrivateIdentity(MockObjectCreator.getPrivateIdentitys().get(0), MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID);		
		assertThat(subscriber, is(IsNull.nullValue()));
	}
	
	@Test
	public void searchSubscriberByPrivateIdentity_SearchPrivateIdentityWithNoSubscriber_ExpectsNullSubscriber() throws Exception {

		when(commonService.populateSoapMessageProperties(soapMessage)).thenReturn(soapMessage);		
		when(subscriberProxy.sendAndReceive(soapMessage, MockObjectCreator.CORRELATION_ID)).thenReturn("");
		
		Subscriber subscriber = commonSubscriberHandler.searchSubscriberByPrivateIdentity(MockObjectCreator.getPrivateIdentitys().get(0), MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID);
		assertThat(subscriber, is(IsNull.nullValue()));
	}
	
	@Test
	public void searchSubscriber_SearchForExistingPublicIdentity_ExpectsValidSpmlResponse() throws Exception {
			
		when(commonService.createSearchRequest(MockObjectCreator.PUBLIC_SEARCH_NAME, MockObjectCreator.PHONE_NUMBER,
				MockObjectCreator.SITE, false)).thenReturn(MockObjectCreator.getSearchRequestForPublicIdentity());
		
		when(serializableConfig.marshall(MockObjectCreator.getSearchRequestForPublicIdentity())).thenReturn(MockObjectCreator.getPublicKeyStringSearchRequest());
		
		when(commonService.populateSoapMessageProperties(soapMessage)).thenReturn(soapMessage);
		when(subscriberProxy.sendAndReceive(soapMessage, MockObjectCreator.CORRELATION_ID)).thenReturn(MockObjectCreator.getStringSearchResponse());
		when(serializableConfig.unmarshall(MockObjectCreator.getStringSearchResponse())).thenReturn(MockObjectCreator.getSpmlResponse());
		
		SpmlResponse response = commonSubscriberHandler.searchSubscriber(MockObjectCreator.PUBLIC_SEARCH_NAME, MockObjectCreator.PHONE_NUMBER,
				MockObjectCreator.SITE, MockObjectCreator.CORRELATION_ID, false);
		
		assertThat(response.getResult(), is(equalToIgnoringCase(Status.SUCCESS.name())));
	}
	
	@Test
	public void createResponse_GenerateDigitalPhoneResponse_ExpectsSuccessDigitalPhoneResponse() throws Exception {
		
		DigitalPhoneResponse response = commonSubscriberHandler.createResponse(MockObjectCreator.getSpmlResponse());
		assertThat(response.getStatus(), is(Status.CREATED));
	}
	
	@Test
	public void createResponse_GenerateDigitalPhoneResponse_ExpectsFailureDigitalPhoneResponse() throws Exception {
		
		DigitalPhoneResponse failedResponse = commonSubscriberHandler.createResponse(MockObjectCreator.getFailedSpmlResponse());
		assertThat(failedResponse.getStatus(), is(Status.FAILURE));
	}
	
	@Test
	public void createAddRequest_GenerateAddRequestObject_ExpectsValidAddRequest() throws Exception {
		
		when(commonService.createAddRequest(MockObjectCreator.getDigitalPhone())).thenReturn(MockObjectCreator.getAddRequest());
		
		AddRequest addRequest = commonSubscriberHandler.createAddRequest(MockObjectCreator.getDigitalPhone());
		assertThat(addRequest.getVersion(), is("HSS_SUBSCRIBER_v82"));
	}
	
	@Test
	public void createDeleteRequest_GenerateDeleteRequestObject_ExpectsValidDeleteRequest() throws Exception {
		
		when(commonService.createDeleteRequest(MockObjectCreator.SUBSCRIBER_ID)).thenReturn(MockObjectCreator.getDeleteRequest());
		
		DeleteRequest deleteRequest = commonSubscriberHandler.createDeleteRequest(MockObjectCreator.SUBSCRIBER_ID);
		assertThat(deleteRequest.getVersion(), is("HSS_SUBSCRIBER_v82"));
		assertThat(deleteRequest.getIdentifier(), is(MockObjectCreator.SUBSCRIBER_ID));
	}
}
