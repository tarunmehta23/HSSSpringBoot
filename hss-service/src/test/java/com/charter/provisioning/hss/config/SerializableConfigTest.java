package com.charter.provisioning.hss.config;

import com.charter.provisioning.hss.common.MockObjectCreator;
import com.charter.provisioning.network.hss.subscriber.spml.SpmlResponse;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SerializableConfigTest {

	@InjectMocks
	private SerializableConfig serializeConfig;
	
	@Mock
	private SpmlConfig spmlConfig;
	
	@Mock
	private HssServiceConfig serviceConfig;
	
	@Before
	public void setup() throws Exception {
		
		when(spmlConfig.getSpml_namespace()).thenReturn("urn:siemens:names:prov:gw:SPML:2:0");
		when(spmlConfig.getSpml_prefix()).thenReturn("spml");
		when(spmlConfig.getSpml_add_request()).thenReturn("addRequest");
		when(spmlConfig.getSpml_add_response()).thenReturn("addResponse");
		when(spmlConfig.getSpml_search_request()).thenReturn("searchRequest");
		when(spmlConfig.getSpml_search_response()).thenReturn("searchResponse");
		when(spmlConfig.getSpml_modify_request()).thenReturn("modifyRequest");
		when(spmlConfig.getSpml_modify_response()).thenReturn("modifyResponse");
		when(spmlConfig.getSpml_delete_request()).thenReturn("deleteRequest");
		when(spmlConfig.getSpml_delete_response()).thenReturn("deleteResponse");
		when(serviceConfig.getDefault_subscriber_ns()).thenReturn("urn:siemens:names:prov:gw:HSS_SUBSCRIBER:7:0");
		when(spmlConfig.getNs2_rrefix()).thenReturn("ns2");
		when(spmlConfig.getXsi_namespace()).thenReturn("http://www.w3.org/2001/XMLSchema-instance");
		when(spmlConfig.getXsi_rrefix()).thenReturn("xsi");
		when(spmlConfig.getNs2_subscriber()).thenReturn("ns2:Subscriber");
		when(spmlConfig.getObjects()).thenReturn("objects");
		when(spmlConfig.getObject()).thenReturn("object");
		when(spmlConfig.getGlobal_filter_id()).thenReturn("globalFilterId");
		when(spmlConfig.getGlobal_filter()).thenReturn("globalFilter");
		when(spmlConfig.getSubscriber()).thenReturn("subscriber");
		when(spmlConfig.getXmlns_subscriber()).thenReturn("xmlns:subscriber");
		when(spmlConfig.getXsi_rrefix()).thenReturn("xsi");
		when(spmlConfig.getXmlns_xsi()).thenReturn("xmlns:xsi");
		when(spmlConfig.getType()).thenReturn("type");
		when(spmlConfig.getXsiType()).thenReturn("xsi:type");
        when(spmlConfig.getNew_generatedString()).thenReturn("newGenerated");
		when(spmlConfig.getReturn_resulting_objectString()).thenReturn("returnResultingObject");
		when(spmlConfig.getDelete_scope_string()).thenReturn("deleteScope");
		when(spmlConfig.getExecution()).thenReturn("execution");
		when(spmlConfig.getLanguage()).thenReturn("language");
		when(spmlConfig.getExecution_time()).thenReturn("executionTime");
        when(spmlConfig.getRequest_id()).thenReturn("requestID");
        when(spmlConfig.getResult()).thenReturn("result");
        when(spmlConfig.getSearch_status()).thenReturn("searchStatus"); 
        when(spmlConfig.getXmlns_xsi_attr()).thenReturn("xmlnsXSI");
		when(spmlConfig.getName()).thenReturn("name");
		when(spmlConfig.getXsi_type_attr()).thenReturn("xsiType");
		when(spmlConfig.getValue()).thenReturn("value");
		when(spmlConfig.getPrivate_user_id()).thenReturn("privateUserId"); 
        when(spmlConfig.getImplicit_registered_set()).thenReturn("implicitRegisteredSet");
		when(spmlConfig.getPublic_user_id()).thenReturn("publicUserId");
		when(spmlConfig.getService_profile()).thenReturn("serviceProfile");
		when(spmlConfig.getGlobal_filter_id()).thenReturn("globalFilterId");
		
		when(spmlConfig.getOperation()).thenReturn("operation");
		when(spmlConfig.getScope()).thenReturn("scope");
		when(spmlConfig.getModification()).thenReturn("modification");
	}
	
	@Test
	public void testSerilizableMarshallSuccess() throws Exception {
		
		String marshallString = serializeConfig.marshall(MockObjectCreator.getSearchRequestForPublicIdentity());
		Assert.assertThat(marshallString, CoreMatchers.containsString("8216328886"));
	}
	
	@Test
	public void marshall_MarshallAddRequest_ExpectsStringXML() throws Exception {
		
		String marshallString = serializeConfig.marshall(MockObjectCreator.getAddRequest());
		Assert.assertThat(marshallString, CoreMatchers.containsString("xsi:type=\"subscriber:Subscriber\""));
	}
	
	@Test
	public void unmarshall_UnmarshallSearchResponseStringXML_ExpectsSpmlResponseObject() throws Exception {
		
		when(spmlConfig.getSpml_ext()).thenReturn("spml:");
		when(spmlConfig.getCdata()).thenReturn("<!\\\\[CDATA\\\\[([^]]+)\\\\]\\\\]>");
		
		String responeBody = StringUtils.substringBetween(MockObjectCreator.getStringSearchResponse(), "<soapenv:Body>","</soapenv:Body>");
		
		SpmlResponse response = serializeConfig.unmarshall(responeBody);
		Assert.assertThat(response.getSubscriber().getIdentifier(), is("13718275614005466511585250035104"));
	}

	@Test
	public void unmarshall_UnmarshallModifyResponseStringXML_ExpectsSpmlResponseObject() throws Exception {

		when(spmlConfig.getSpml_ext()).thenReturn("spml:");
		when(spmlConfig.getCdata()).thenReturn("<!\\\\[CDATA\\\\[([^]]+)\\\\]\\\\]>");

		when(spmlConfig.getOperation()).thenReturn("operation");
		when(spmlConfig.getScope()).thenReturn("scope");
		when(spmlConfig.getModification()).thenReturn("modification");
		
		String responeBody = MockObjectCreator.getStringModifyResponse();

		SpmlResponse response = serializeConfig.unmarshall(responeBody);
		Assert.assertThat(response.getIdentifier(), is("14786136245232146014940388441980"));
	}
	
	@Test
	public void unmarshall_UnmarshallNullString_ExpectsNullString() throws Exception {

		SpmlResponse response = serializeConfig.unmarshall(null);
		Assert.assertThat(response, is(IsNull.nullValue()));
	}
}
