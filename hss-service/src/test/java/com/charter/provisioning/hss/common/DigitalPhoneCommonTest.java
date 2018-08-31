package com.charter.provisioning.hss.common;

import com.charter.provisioning.hss.config.HssServiceConfig;
import com.charter.provisioning.hss.config.SpmlConfig;
import com.charter.provisioning.hss.model.Feature;
import com.charter.provisioning.hss.model.Features;
import com.charter.provisioning.hss.model.PublicIdentity;
import com.charter.provisioning.network.hss.subscriber.spml.AddRequest;
import com.charter.provisioning.network.hss.subscriber.spml.DeleteRequest;
import com.charter.provisioning.network.hss.subscriber.spml.ModifyRequest;
import com.charter.provisioning.network.hss.subscriber.spml.SearchRequest;
import com.charter.provisioning.network.hss.subscriber.spml.schema.GlobalFilterId;
import com.charter.provisioning.network.hss.subscriber.spml.schema.Modification;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DigitalPhoneCommonTest {

	@InjectMocks
	private DigitalPhoneCommon commonService;
	
	@Mock
	private SpmlConfig spmlConfig;

	@Mock
	private ServiceUtil serviceUtil;

	@Mock
	private HssServiceConfig serviceConfig;
	
	@Test
	public void createSearchRequest_ForExistingPublicIdentity_ExpectsSearchRequestObject() throws Exception {
		
		when(spmlConfig.getPublic_identity_search_name()).thenReturn(MockObjectCreator.PUBLIC_SEARCH_NAME);
		when(serviceConfig.getSipPrefix()).thenReturn("sip:");
		when(serviceConfig.getE164DigitPrefix()).thenReturn("+1");
		when(serviceConfig.getVersion()).thenReturn("HSS_SUBSCRIBER_v82");
		when(spmlConfig.getXsi_namespace()).thenReturn("http://www.w3.org/2001/XMLSchema-instance");
		
		SearchRequest searchRequest = commonService.createSearchRequest(MockObjectCreator.PUBLIC_SEARCH_NAME, MockObjectCreator.PHONE_NUMBER, MockObjectCreator.SITE, true);
		
		assertThat(searchRequest.getXsi(), is("http://www.w3.org/2001/XMLSchema-instance"));
		assertThat(searchRequest.getVersion(), is("HSS_SUBSCRIBER_v82"));
	}

	@Test
	public void createSearchRequest_ForExistingNationalPublicIdentity_ExpectsSearchRequestObject() throws Exception {

		when(spmlConfig.getPublic_identity_search_name()).thenReturn(MockObjectCreator.PUBLIC_SEARCH_NAME);
		when(serviceConfig.getSipPrefix()).thenReturn("sip:");
		when(serviceConfig.getVersion()).thenReturn("HSS_SUBSCRIBER_v82");
		when(spmlConfig.getXsi_namespace()).thenReturn("http://www.w3.org/2001/XMLSchema-instance");

		SearchRequest searchRequest = commonService.createSearchRequest(MockObjectCreator.PUBLIC_SEARCH_NAME, MockObjectCreator.PHONE_NUMBER, MockObjectCreator.SITE, false);

		assertThat(searchRequest.getXsi(), is("http://www.w3.org/2001/XMLSchema-instance"));
		assertThat(searchRequest.getVersion(), is("HSS_SUBSCRIBER_v82"));
	}
	
	@Test
	public void createSearchRequest_ForExistingPrivateIdentity_ExpectsSearchRequestObject() throws Exception {

		when(spmlConfig.getPublic_identity_search_name()).thenReturn("impi");
		when(serviceConfig.getVersion()).thenReturn("HSS_SUBSCRIBER_v82");
		when(spmlConfig.getXsi_namespace()).thenReturn("http://www.w3.org/2001/XMLSchema-instance");
		
		SearchRequest searchRequest = commonService.createSearchRequest(MockObjectCreator.PUBLIC_SEARCH_NAME, MockObjectCreator.PHONE_NUMBER, MockObjectCreator.SITE, false);
		
		assertThat(searchRequest.getXsi(), is("http://www.w3.org/2001/XMLSchema-instance"));
		assertThat(searchRequest.getVersion(), is("HSS_SUBSCRIBER_v82"));
	}
	
	@Test
	public void createAddRequest_CreatesAddRequestObject_ExpectsAddRequestObject() throws Exception {
		
		when(serviceConfig.getSubscriber()).thenReturn("urn:siemens:names:prov:gw:HSS_SUBSCRIBER:8:2");
		when(serviceConfig.getVersion()).thenReturn("HSS_SUBSCRIBER_v82");
		when(spmlConfig.getNew_generated()).thenReturn("true");
		when(spmlConfig.getDefault_language()).thenReturn("en_us");
		when(spmlConfig.getReturn_resulting_object()).thenReturn("none");
		when(spmlConfig.getSubscriber_type()).thenReturn("subscriber:Subscriber");
		when(spmlConfig.getXsi_namespace()).thenReturn("http://www.w3.org/2001/XMLSchema-instance");
		when(spmlConfig.getSubscriber_id_length()).thenReturn(32);
		when(serviceUtil.appendRandom(32, false)).thenReturn("13718275614005466511585250035104");
		when(spmlConfig.getIrs_suffix_length()).thenReturn(16);
		when(serviceUtil.appendRandom(16, false)).thenReturn("1601409354118132");
		when(serviceConfig.getImplicitRegSetPrefix()).thenReturn("irs");
		when(serviceConfig.getServiceProfileNamePrefix()).thenReturn("sp");
		when(serviceConfig.getSipPrefix()).thenReturn("sip:");
		when(serviceConfig.getOperationCreate()).thenReturn("create");
		when(serviceConfig.getFeatureBlock()).thenReturn("BLOCK");
		when(serviceConfig.getHGroup()).thenReturn(MockObjectCreator.HGROUP);
		when(serviceConfig.getBGroup()).thenReturn(MockObjectCreator.BGROUP);
		
		AddRequest addRequest = commonService.createAddRequest(MockObjectCreator.getDigitalPhone());
		
		assertThat(addRequest.getSubscriber(), is("urn:siemens:names:prov:gw:HSS_SUBSCRIBER:8:2"));
		assertThat(addRequest.getVersion(), is("HSS_SUBSCRIBER_v82"));
		
		addRequest.getObject().getHss().getServiceProfile().get(0).getGlobalFilterId();
	}
	
	@Test
	public void createAddRequest_CreatesAddRequestObject_ExpectsResidentialAddRequestObject() throws Exception {

		when(serviceConfig.getSubscriber()).thenReturn("urn:siemens:names:prov:gw:HSS_SUBSCRIBER:8:2");
		when(serviceConfig.getVersion()).thenReturn("HSS_SUBSCRIBER_v82");
		when(spmlConfig.getNew_generated()).thenReturn("true");
		when(spmlConfig.getDefault_language()).thenReturn("en_us");
		when(spmlConfig.getReturn_resulting_object()).thenReturn("none");
		when(spmlConfig.getSubscriber_type()).thenReturn("subscriber:Subscriber");
		when(spmlConfig.getXsi_namespace()).thenReturn("http://www.w3.org/2001/XMLSchema-instance");
		when(spmlConfig.getSubscriber_id_length()).thenReturn(32);
		when(serviceUtil.appendRandom(32, false)).thenReturn("13718275614005466511585250035104");
		when(spmlConfig.getIrs_suffix_length()).thenReturn(16);
		when(serviceUtil.appendRandom(16, false)).thenReturn("1601409354118132");
		when(serviceConfig.getImplicitRegSetPrefix()).thenReturn("irs");
		when(serviceConfig.getServiceProfileNamePrefix()).thenReturn("sp");
		when(serviceConfig.getSipPrefix()).thenReturn("sip:");
		when(serviceConfig.getOperationCreate()).thenReturn("create");
		when(serviceConfig.getFeatureBlock()).thenReturn("BLOCK");

		when(serviceConfig.isResidentialService("DP01")).thenReturn(true);
		when(serviceConfig.getFeatures()).thenReturn(MockObjectCreator.getHssFeature());
		when(serviceConfig.getHssFilter()).thenReturn("hssFilter");

		AddRequest addRequest = commonService.createAddRequest(MockObjectCreator.getResidentialDigitalPhone());

		Assert.assertThat(addRequest.getObject(), is(IsNull.notNullValue()));
		Assert.assertThat(addRequest.getObject().getHss(), is(IsNull.notNullValue()));
		Assert.assertThat(addRequest.getObject().getHss().getServiceProfile(), is(IsNull.notNullValue()));
		Assert.assertThat(addRequest.getObject().getHss().getServiceProfile().get(0), is(IsNull.notNullValue()));
		Assert.assertThat(addRequest.getObject().getHss().getServiceProfile().get(0).getGlobalFilterId(),
				is(IsNull.notNullValue()));

		List<String> globalFilterIds = addRequest.getObject().getHss().getServiceProfile().get(0).getGlobalFilterId()
				.stream().map(GlobalFilterId::getGlobalFilterId).collect(Collectors.toList());

		assertThat(globalFilterIds, hasItems("900COS"));
		assertThat(globalFilterIds, hasItems("INTLCOS"));
		assertThat(globalFilterIds, hasItems("MUT-DCA01q"));
	}
	
	@Test
	public void createAddRequest_CreatesAddRequestWithoutPublicAndPrivateIdentity_ExpectsAddRequestObject() throws Exception {
		
		when(serviceConfig.getSubscriber()).thenReturn("urn:siemens:names:prov:gw:HSS_SUBSCRIBER:8:2");
		when(serviceConfig.getVersion()).thenReturn("HSS_SUBSCRIBER_v82");
		when(spmlConfig.getNew_generated()).thenReturn("true");
		when(spmlConfig.getDefault_language()).thenReturn("en_us");
		when(spmlConfig.getReturn_resulting_object()).thenReturn("none");
		when(spmlConfig.getSubscriber_type()).thenReturn("subscriber:Subscriber");
		when(spmlConfig.getXsi_namespace()).thenReturn("http://www.w3.org/2001/XMLSchema-instance");
		when(spmlConfig.getSubscriber_id_length()).thenReturn(32);
		when(serviceUtil.appendRandom(32, false)).thenReturn("13718275614005466511585250035104");
		
		AddRequest addRequest = commonService.createAddRequest(MockObjectCreator.getDigitalPhoneWithoutPublicAndPrivateIdentity());
		
		assertThat(addRequest.getSubscriber(), is("urn:siemens:names:prov:gw:HSS_SUBSCRIBER:8:2"));
		assertThat(addRequest.getVersion(), is("HSS_SUBSCRIBER_v82"));
	}
	
	@Test
	public void createDeleteRequest_CreatesDeleteRequestObject_ExpectsDeleteRequestObject() throws Exception {
		
		when(serviceConfig.getVersion()).thenReturn("HSS_SUBSCRIBER_v82");
		when(spmlConfig.getDelete_scope()).thenReturn("all");
		when(spmlConfig.getSynchronous()).thenReturn("synchronous");
		when(spmlConfig.getDefault_language()).thenReturn("en_us");
		when(spmlConfig.getReturn_resulting_object()).thenReturn("returnResultingObject");
		when(spmlConfig.getObject_class()).thenReturn("Subscriber");
		
		
		DeleteRequest deleteRequest = commonService.createDeleteRequest(MockObjectCreator.SUBSCRIBER_ID);
		
		assertThat(deleteRequest.getIdentifier(), is(MockObjectCreator.SUBSCRIBER_ID));
		assertThat(deleteRequest.getVersion(), is("HSS_SUBSCRIBER_v82"));
	}
	
	@Test
	public void populateSoapMessageProperties_SetSoapProperties_ExpectsSoapMessageWithProperties() throws Exception {
		
		when(serviceConfig.getUrl()).thenReturn("endpoint.url");
		when(serviceConfig.getEndPointURL()).thenReturn("http://localhost:8096/HssSubscriber82Service");
		when(serviceConfig.getContentType()).thenReturn("http.content.type");
		when(serviceConfig.getContentTypeTextXML()).thenReturn("text/xml");
		when(serviceConfig.getMethodType()).thenReturn("http.method.type");
		when(serviceConfig.getPostMethod()).thenReturn("http.method.post");
		when(serviceConfig.getSoapActionHeader()).thenReturn("Subscriber");
		
		
		SoapMessage message = commonService.populateSoapMessageProperties(MockObjectCreator.getSoapMessage());
		
		assertThat(message.getPropertyValue("endpoint.url"), is("http://localhost:8096/HssSubscriber82Service"));
	}
	
	@Test
	public void createPublicIdentityModification_CreateModificationObject_ExpectsModificationObject() throws Exception {
		
		when(serviceConfig.getSipPrefix()).thenReturn("sip:");
		when(serviceConfig.getDefaultSiteDomain()).thenReturn("ims.eng.rr.com");
		when(spmlConfig.getSpml_op_setoradd()).thenReturn("setoradd");
		when(spmlConfig.getSubscriber_public_user_id()).thenReturn("subscriber:PublicUserId");
		
		Modification modification = commonService.createPublicIdentityModification(PublicIdentity.builder().userId(MockObjectCreator.PHONE_NUMBER).build(), "profileName", "irsId", "FALSE");
	
		assertThat(modification.getOperation(), is("setoradd"));
		assertThat(modification.getMatch().getOriginalPublicUserId(), is("sip:"+MockObjectCreator.PHONE_NUMBER+"@ims.eng.rr.com"));
	}
	
	@Test
	public void createModifyRequest_CreateModifyRequestObject_ExpectsModifyRequestObject() throws Exception {
		
		ModifyRequest modifyRequest = commonService.createModifyRequest(MockObjectCreator.CORRELATION_ID);
		assertThat(modifyRequest.getIdentifier(), is(MockObjectCreator.CORRELATION_ID));
	}
	
	@Test
	public void getFeaturesByName_GetValidFeatureList_ExpectsListOfFeatures() throws Exception {
		
		Features feature = new Features();
		feature.setName(MockObjectCreator.BGROUP);
		
		when(serviceConfig.getFeaturesByName(MockObjectCreator.BGROUP)).thenReturn(Stream.of(feature).collect(Collectors.toList()));
		when(serviceConfig.getOperationCreate()).thenReturn(MockObjectCreator.OPERATION_CREATE);
		
		List<Feature> featureList = commonService.getFeaturesByName(MockObjectCreator.BGROUP);
		
		assertThat(featureList.size(), is(1));
		assertThat(featureList.get(0).getName(), is(MockObjectCreator.BGROUP));
	}
	
	@Test
	public void generate32CharTimestampRandomKey_getTimeStampRandomKey_ExpectsNonNullString() throws Exception {
		
		when(spmlConfig.getSubscriber_id_length()).thenReturn(32);
		when(serviceUtil.appendTimestamp(32, true)).thenReturn(MockObjectCreator.CORRELATION_ID);
		
		String random = commonService.generate32CharTimestampRandomKey();
		assertThat(random, is(MockObjectCreator.CORRELATION_ID));
	}
}
