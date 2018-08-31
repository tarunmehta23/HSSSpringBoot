package com.charter.provisioning.hss.common;

import com.charter.provisioning.hss.exception.SoapServiceException;
import com.charter.provisioning.hss.model.*;
import com.charter.provisioning.hss.model.DigitalPhoneResponse.Status;
import com.charter.provisioning.network.hss.subscriber.spml.AddRequest;
import com.charter.provisioning.network.hss.subscriber.spml.DeleteRequest;
import com.charter.provisioning.network.hss.subscriber.spml.ModifyRequest;
import com.charter.provisioning.network.hss.subscriber.spml.SearchRequest;
import com.charter.provisioning.network.hss.subscriber.spml.SpmlResponse;
import com.charter.provisioning.network.hss.subscriber.spml.schema.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MockObjectCreator {

    public static final String PACKAGE = "BC01";
    
    public static final String RESIDENTIAL_PACKAGE = "DP01";

    public static final String CORRELATION_ID = "0b767cdf-ec4a-403f-9c41-e71b703532a3";

    public static final String PHONE_NUMBER = "8216328886";

	public static final String PRIVATE_IDENTITY = "219CF751C13481P5";

	public static final String NATIONAL_PUBLIC_IDENTITY = "mlhg_409077_0001";
    
    public static final String ORIGINAL_PUBLIC_USER_ID = "sip:+18216328886@ims.eng.rr.com";

    public static final String USER_NAME = "jdoe";

    public static final String PASSWORD = "test123";

    public static final String IRS_SUFFIX_ID = "01623E63117EB61561AD55AE4C072CA4";

    public static final String SITE = "DV2";

    public static final String SUBSCRIBER_ID = "C61561BD55AE4C072C92";
    
    public static final String OPERATION_CREATE = "create";
    
    public static final String OPERATION_DELETE = "delete";
    
    public static final String OPERATION_UPDATE = "update";
    
    public static final String PUBLIC_SEARCH_NAME = "impu";

	public static final String DPHONE = "DPHONE";
    
    public static final String HGROUP = "HGROUP";
    
    public static final String BGROUP = "BGROUP";

	public static DigitalPhone createDigitalPhoneRequest() {

		return DigitalPhone.builder().operation(OPERATION_CREATE).site(SITE)
				.featurePackage(PACKAGE).profile(Profile.builder().tas("DCA01q").features(createFeatures()).build())
				.publicIdentity(getPublicIdentitys())
				.privateIdentity(getPrivateIdentitys())
				.build();
	}

	public static DigitalPhoneResponse createSuccessDigitalPhoneResponse() {

		return DigitalPhoneResponse.builder().status(Status.CREATED).build();
	}
	
	public static DigitalPhoneResponse getDigitalPhoneResponseWithSuccessStatus() {

		return DigitalPhoneResponse.builder().status(Status.SUCCESS).build();
	}
	
	public static DigitalPhoneResponse createFailedDigitalPhoneResponse() {

		return DigitalPhoneResponse.builder().status(Status.FAILURE).build();
	}
	
	public static DigitalPhone createInvalidDigitalPhoneRequest() {

		return DigitalPhone.builder().operation(OPERATION_CREATE).site(SITE)
				.profile(Profile.builder().tas("DCA01q").features(createFeatures()).build()).build();
	}
	
	public static List<Features> getHssFeature() {
		
		List<Features> features = new ArrayList<>();
		
		Features feature1 = new Features();
		feature1.setName("900");
		feature1.setActionName("hssFilter");
		feature1.setActionValue(Stream.of("900COS").collect(Collectors.toList()));
		
		Features feature2 = new Features();
		feature2.setName("INTL");
		feature2.setActionName("hssFilter");
		feature2.setActionValue(Stream.of("INTLCOS").collect(Collectors.toList()));
		
		Features feature3 = new Features();
		feature3.setName("DP01");
		feature3.setActionName("hssFilter");
		feature3.setActionValue(Stream.of("CID").collect(Collectors.toList()));
		
		Features feature4 = new Features();
		feature4.setName("DP01");
		feature4.setActionName("imsServiceProfilePrefix");
		feature4.setActionValue(Stream.of("MUT","REG","MO").collect(Collectors.toList()));

		features.add(feature1);
		features.add(feature2);
		features.add(feature3);
		features.add(feature4);
		
		return features;
	}

	private static List<Feature> createFeatures() {
		
		List<Feature> features = new ArrayList<>();
		features.add(Feature.builder().operation(OPERATION_CREATE).name("BC01").build());
		features.add(Feature.builder().operation(OPERATION_CREATE).name("package").value("BC01").build());
		features.add(Feature.builder().operation(OPERATION_CREATE).name("BLOCK").featureProperties(createFeatureProperties()).build());
		
		return features;
	}
	
	private static List<Feature> createResidentialFeatures() {
		
		List<Feature> features = new ArrayList<>();
		features.add(Feature.builder().operation(OPERATION_CREATE).name("DP01").build());
		features.add(Feature.builder().operation(OPERATION_CREATE).name("package").build());
		features.add(Feature.builder().operation(OPERATION_CREATE).name("BLOCK").featureProperties(createFeatureProperties()).build());
		
		return features;
	}

	private static List<FeatureProperty> createFeatureProperties() {

		List<FeatureProperty> featureProperties = new ArrayList<>();
		featureProperties.add(FeatureProperty.builder().operation(OPERATION_CREATE).name("900").build());
		featureProperties.add(FeatureProperty.builder().operation(OPERATION_CREATE).name("INTL").build());
		
		return featureProperties;
	}

	public static List<Feature> createHGFeatures() {

		List<Feature> features = new ArrayList<>();
		features.add(Feature.builder().operation(OPERATION_CREATE).name("MO-DCA011-UNREG").build());
		features.add(Feature.builder().operation(OPERATION_CREATE).name("MUT-DCA011-UNREG").build());

		return features;
	}

	public static List<PublicIdentity> getPublicIdentitys() {
		return Stream.of(PublicIdentity.builder().operation(OPERATION_CREATE).userId(PHONE_NUMBER).build())
				.collect(Collectors.toList());
	}
	
	public static List<PublicIdentity> getPublicIdentitysForDelete() {
		return Stream.of(PublicIdentity.builder().operation(OPERATION_DELETE).userId(PHONE_NUMBER).build())
				.collect(Collectors.toList());
	}

	public static List<PublicIdentity> getHGPublicIdentityList() {

		List<PublicIdentity> publicIdentityList = new ArrayList<>();

		publicIdentityList.add(PublicIdentity.builder().userId(PHONE_NUMBER).build());
		publicIdentityList.add(PublicIdentity.builder().operation(OPERATION_CREATE).userId("mlhg_409077_0000").build());
		publicIdentityList.add(PublicIdentity.builder().operation(OPERATION_CREATE).userId(NATIONAL_PUBLIC_IDENTITY).serviceId(PHONE_NUMBER).build());
		return publicIdentityList;
	}
	
	public static List<PublicIdentity> getBGPublicIdentityList() {

		return Stream.of(PublicIdentity.builder().operation(OPERATION_CREATE).userId("pickup_group_123456").build())
				.collect(Collectors.toList());
	}
	
	public static List<PublicIdentity> getHGControllerPublicIdentityList() {

		return Stream.of(PublicIdentity.builder().operation(OPERATION_CREATE).userId("mlhg_409077_0000").build())
				.collect(Collectors.toList());
	}

	public static List<PrivateIdentity> getPrivateIdentitys() {
		return Stream.of(PrivateIdentity.builder().operation(OPERATION_CREATE).userId("219BF751A12481C6")
				.password("01623E63117EB61561AD55AE4C072CA4").build()).collect(Collectors.toList());
	}

	public static Subscriber getSubscriber() {
		
		return Subscriber.builder()
				.type("subscriber:Subscriber")
				.xsi("http://www.w3.org/2001/XMLSchema-instance")
				.identifier(SUBSCRIBER_ID)
				// Prepare HSS spml request
				.hss(getHSSRequest())
				.build();
	}
	
	public static Subscriber getHGSubscriber() {
		
		return Subscriber.builder()
				.type("subscriber:Subscriber")
				.xsi("http://www.w3.org/2001/XMLSchema-instance")
				.identifier(SUBSCRIBER_ID)
				// Prepare HSS spml request
				.hss(getHSSHGRequest())
				.build();
	}
	
	public static Subscriber getSubscriberWithMultiplePublicUserIds() {
		
		return Subscriber.builder()
				.type("subscriber:Subscriber")
				.xsi("http://www.w3.org/2001/XMLSchema-instance")
				.identifier(SUBSCRIBER_ID)
				// Prepare HSS spml request
				.hss(getHSSRequestWithMultiplePublicIds())
				.build();
	}
	
	public static Subscriber getInvalidSubscriber() {
		
		return Subscriber.builder()
				.type("subscriber:Subscriber")
				.xsi("http://www.w3.org/2001/XMLSchema-instance")
				.identifier(SUBSCRIBER_ID)
				.build();
	}
	
	public static Subscriber getInvalidSubscriberWithNullPublicId() {
		
		return Subscriber.builder()
				.type("subscriber:Subscriber")
				.xsi("http://www.w3.org/2001/XMLSchema-instance")
				.identifier(SUBSCRIBER_ID)
				.hss(getHSSRequestWithNullPublicuserId())
				.build();
	}
	
	public static Subscriber getInvalidSubscriberWithEmptyPublicId() {
		
		return Subscriber.builder()
				.type("subscriber:Subscriber")
				.xsi("http://www.w3.org/2001/XMLSchema-instance")
				.identifier(SUBSCRIBER_ID)
				.hss(getHSSRequestWithEmptylPublicuserId())
				.build();
	}

	public static Modification getPublicIdentityModification() {

		return Modification.builder()
				.operation("setoradd")
				.match(Match.builder()
						.type("subscriber:PublicUserId")
						.originalPublicUserId("8216328886@ims.eng.rr.com")
						.irsId("irsId")
						.build())
				.valueObject(ValueObject.builder()
						.type("subscriber:PublicUserId")
						.originalPublicUserId("8216328886@ims.eng.rr.com")
						.defaultIndication("FALSE")
						.serviceProfileName("serviceProfileName")
						.irsId("irsId")
						.build())
				.build();
	}
	

	private static Hss getHSSRequest() {


		ImplicitRegisteredSet implicitRegisteredSet = createImplicitRegisteredSet();
		ServiceProfile serviceProfile = getServiceProfile();

		return Hss.builder()

				.subscriptionId("1").profileType("normal")
				.adminBlocked("false")
				.defaultScscfRequired("true").ccfPrimary("primaryccf.ims.rr.com")
				.ccfSecondary("secondaryccf.ims.rr.com")

				.implicitRegisteredSet(Stream.of(implicitRegisteredSet).collect(Collectors.toList()))
				.serviceProfile(Stream.of(serviceProfile).collect(Collectors.toList()))

				.privateUserId(getPrivateUserIds())
				.publicUserId(getSubscriberPublicUserIds())

				.build();
	}
	
	private static Hss getHSSHGRequest() {

		return Hss.builder()

				.subscriptionId("1").profileType("normal")
				.adminBlocked("false")
				.defaultScscfRequired("true").ccfPrimary("primaryccf.ims.rr.com")
				.ccfSecondary("secondaryccf.ims.rr.com")

				.privateUserId(getHGPrivateUserIds())
				.publicUserId(getHGPublicUserIds())

				.build();
	}
	
	private static Hss getHSSRequestWithNullPublicuserId() {


		ImplicitRegisteredSet implicitRegisteredSet = createImplicitRegisteredSet();
		ServiceProfile serviceProfile = getServiceProfile();
		
		List<PublicUserId> publicUserIds = new ArrayList<>();
		publicUserIds.add(null);
		publicUserIds.add(PublicUserId.builder().build());
		publicUserIds.add(PublicUserId.builder().defaultIndication("false").build());

		return Hss.builder()

				.subscriptionId("1").profileType("normal")
				.adminBlocked("false")
				.defaultScscfRequired("true").ccfPrimary("primaryccf.ims.rr.com")
				.ccfSecondary("secondaryccf.ims.rr.com")

				.implicitRegisteredSet(Stream.of(implicitRegisteredSet).collect(Collectors.toList()))
				.serviceProfile(Stream.of(serviceProfile).collect(Collectors.toList()))

				.privateUserId(getPrivateUserIds())
				.publicUserId(publicUserIds)

				.build();
	}
	
	private static Hss getHSSRequestWithEmptylPublicuserId() {


		ImplicitRegisteredSet implicitRegisteredSet = createImplicitRegisteredSet();
		ServiceProfile serviceProfile = getServiceProfile();

		return Hss.builder()

				.subscriptionId("1").profileType("normal")
				.adminBlocked("false")
				.defaultScscfRequired("true").ccfPrimary("primaryccf.ims.rr.com")
				.ccfSecondary("secondaryccf.ims.rr.com")

				.implicitRegisteredSet(Stream.of(implicitRegisteredSet).collect(Collectors.toList()))
				.serviceProfile(Stream.of(serviceProfile).collect(Collectors.toList()))

				.privateUserId(getPrivateUserIds())
				

				.build();
	}
	
	private static Hss getHSSRequestWithMultiplePublicIds() {


		ImplicitRegisteredSet implicitRegisteredSet = createImplicitRegisteredSet();
		ServiceProfile serviceProfile = getServiceProfile();

		List<PublicUserId> publicUserIds = new ArrayList<>();
		publicUserIds.addAll(getSubscriberPublicUserIds());
		publicUserIds.add(PublicUserId.builder().defaultIndication("true").originalPublicUserId("sip:8216328889").build());
		
		return Hss.builder()

				.subscriptionId("1").profileType("normal")
				.adminBlocked("false")
				.defaultScscfRequired("true").ccfPrimary("primaryccf.ims.rr.com")
				.ccfSecondary("secondaryccf.ims.rr.com")

				.implicitRegisteredSet(Stream.of(implicitRegisteredSet).collect(Collectors.toList()))
				.serviceProfile(Stream.of(serviceProfile).collect(Collectors.toList()))

				.privateUserId(getPrivateUserIds())
				.publicUserId(publicUserIds)

				.build();
	}
	
	private static List<PublicUserId> getPublicUserIds() {
		
		return Stream.of(PublicUserId.builder().publicUserId(PHONE_NUMBER).irsId("IrsId").build()).collect(Collectors.toList());
	}
	
	private static List<PublicUserId> getSubscriberPublicUserIds() {
		
		return Stream.of(PublicUserId.builder().originalPublicUserId(ORIGINAL_PUBLIC_USER_ID).defaultIndication("true")
				.irsId("IrsId").build()).collect(Collectors.toList());
	}
	
	private static List<PublicUserId> getHGPublicUserIds() {
		
		List<PublicUserId> publicUserIdList = new ArrayList<>();
		publicUserIdList.add(PublicUserId.builder().originalPublicUserId(PHONE_NUMBER).build());
		return publicUserIdList;
	}
	
	private static List<PrivateUserId> getPrivateUserIds() {
		return Stream.of(PrivateUserId.builder().privateUserId("privateUserId")
				.httpDigestKey("httpDigestKey")
				.preferredAuthenticationScheme("PrefAuth")
				.preferredDomain("prefDomain")
				.build()).collect(Collectors.toList());
	}
	
	private static List<PrivateUserId> getHGPrivateUserIds() {
		return Stream.of(PrivateUserId.builder().privateUserId("219CF751C13481P5@ims.eng.rr.com")
				.httpDigestKey("01623E63117EB61561AD55AE4C072CA4")
				.preferredAuthenticationScheme("PrefAuth")
				.preferredDomain("prefDomain")
				.build()).collect(Collectors.toList());
	}

	private static ImplicitRegisteredSet createImplicitRegisteredSet() {
		return ImplicitRegisteredSet.builder().irsId("irs".concat(IRS_SUFFIX_ID)).build();
	}
	
	private static ServiceProfile getServiceProfile() {

		return ServiceProfile.builder()

				.profileName("sp".concat(IRS_SUFFIX_ID))
				.globalFilterId(getGlobalFilterIds())
				
				.subscribedMediaProfileID(
						SubscribedMediaProfileID.builder().sessionReleasePolicy("deregisterNoForcedSessionRelease")
								.forkingPolicy("mixedForking").build())
				.build();
	}
	
	private static List<GlobalFilterId> getGlobalFilterIds() {

		List<GlobalFilterId> globalFilterIds = new ArrayList<>();
		globalFilterIds.add(GlobalFilterId.builder().globalFilterId("MUT-CTS").build());
		globalFilterIds.add(GlobalFilterId.builder().globalFilterId("MO-CTS").build());
		globalFilterIds.add(GlobalFilterId.builder().globalFilterId("CIDBCP").build());
		globalFilterIds.add(GlobalFilterId.builder().globalFilterId("REG-CTS").build());
		return globalFilterIds;
	}

	public static DigitalPhone getDigitalPhone() {
		return DigitalPhone.builder().name(DPHONE).featurePackage(PACKAGE).site(SITE).operation(OPERATION_CREATE)
				.publicIdentity(getPublicIdentitys()).privateIdentity(getPrivateIdentitys()).profile(getProfile()).build();
	}
	
	public static DigitalPhone getHGDigitalPhone() {
		return DigitalPhone.builder().name(HGROUP).site(SITE).operation(OPERATION_CREATE)
				.publicIdentity(getHGPublicIdentityList()).profile(getHGProfile()).build();
	}
	
	public static DigitalPhone getHGDigitalPhoneWithSinglePublicIdentity() {
		return DigitalPhone.builder().name(HGROUP).site(SITE).operation(OPERATION_DELETE)
				.publicIdentity(Stream.of(PublicIdentity.builder().operation(OPERATION_DELETE).userId("mlhg_409077_0000").build()).collect(Collectors.toList()))
				.profile(getHGProfile()).build();
	}
	
	public static DigitalPhone getHGDigitalPhoneWithSingleTerminalPublicIdentity() {
		return DigitalPhone.builder().name(HGROUP).site(SITE).operation(OPERATION_DELETE)
				.publicIdentity(Stream.of(PublicIdentity.builder().operation(OPERATION_DELETE).userId("mlhg_409077_0001").build()).collect(Collectors.toList()))
				.profile(getHGProfile()).build();
	}
	
	public static DigitalPhone getHGDigitalPhoneWithInvalidUserIdPrefix() {
		return DigitalPhone.builder().name(HGROUP).site(SITE).operation(OPERATION_CREATE)
				.publicIdentity(Stream.of(PublicIdentity.builder().operation(OPERATION_CREATE).userId("mhg_40@077_0000").build()).collect(Collectors.toList())).build();
	}
	
	public static DigitalPhone getHGDigitalPhoneWithInvalidUserIdLength() {
		return DigitalPhone.builder().name(HGROUP).site(SITE).operation(OPERATION_CREATE)
				.publicIdentity(Stream.of(PublicIdentity.builder().operation(OPERATION_CREATE).userId("mlhg_409077_00004").build()).collect(Collectors.toList())).build();
	}
	
	public static DigitalPhone getHGDigitalPhoneWithInvalidUserId() {
		return DigitalPhone.builder().name(HGROUP).site(SITE).operation(OPERATION_CREATE)
				.publicIdentity(Stream.of(PublicIdentity.builder().operation(OPERATION_CREATE).userId("mlhg_40@077_0000").build()).collect(Collectors.toList())).build();
	}
	
	public static DigitalPhone getHGDigitalPhoneWithMultiplePublicIdentities() {
		return DigitalPhone.builder().name(HGROUP).site(SITE).operation(OPERATION_CREATE)
				.publicIdentity(Stream.of(PublicIdentity.builder().operation(OPERATION_CREATE).userId("mlhg_409077_0000").build(),
						PublicIdentity.builder().operation(OPERATION_CREATE).userId("mlhg_409077_0001").build()).collect(Collectors.toList())).build();
	}
	
	public static DigitalPhone getHGControllerDigitalPhone() {
		return DigitalPhone.builder().name(HGROUP)
				.publicIdentity(getHGControllerPublicIdentityList()).profile(getHGControllerProfile()).privateIdentity(getPrivateIdentitys()).build();
	}
	
	public static DigitalPhone getBGDigitalPhone() {
		return DigitalPhone.builder().featurePackage(PACKAGE).site(SITE).operation(OPERATION_CREATE).name(BGROUP)
				.publicIdentity(getBGPublicIdentityList()).profile(getProfile()).build();
	}
	
	public static DigitalPhone getResidentialDigitalPhone() {
		return DigitalPhone.builder().featurePackage(RESIDENTIAL_PACKAGE).site(SITE).operation(OPERATION_CREATE)
				.publicIdentity(getPublicIdentitys()).privateIdentity(getPrivateIdentitys()).profile(getResidentialProfile()).build();
	}
	
	public static DigitalPhone getDigitalPhoneDeleteRequest() {
		return DigitalPhone.builder().featurePackage(PACKAGE).site(SITE).operation(OPERATION_DELETE)
				.publicIdentity(getPublicIdentitysForDelete()).privateIdentity(getPrivateIdentitys()).profile(getProfile()).build();
	}
	
	public static DigitalPhone getDigitalPhoneWithoutPublicAndPrivateIdentity() {
		List<PrivateIdentity> privateIdentitys = new ArrayList<>();
		privateIdentitys.add(null);
		privateIdentitys.add(null);
		
		List<PublicIdentity> publicIdentitys = new ArrayList<>();
		publicIdentitys.add(null);
		publicIdentitys.add(null);
		
		return DigitalPhone.builder().featurePackage(PACKAGE).site(SITE).operation(OPERATION_CREATE)
				.profile(getProfile()).privateIdentity(privateIdentitys).publicIdentity(publicIdentitys).build();
	}
	
	public static DigitalPhone getDigitalPhoneWithInvalidInput() {

		return DigitalPhone.builder().name(DPHONE).featurePackage(RESIDENTIAL_PACKAGE).operation(OPERATION_CREATE)
				.publicIdentity(getPublicIdentitys()).privateIdentity(getPrivateIdentitys()).profile(getProfile()).build();
	}

	public static DigitalPhone getDigitalPhoneWithEmptyName() {
		return DigitalPhone.builder().featurePackage(PACKAGE).build();
	}

	private static Profile getProfile() {
		return Profile.builder().operation(OPERATION_CREATE).tas("DCA01q").features(createFeatures()).build();
	}

	private static Profile getHGProfile() {
		return Profile.builder().operation(OPERATION_CREATE).tas("DCA01q").build();
	}
	
	private static Profile getHGControllerProfile() {
		return Profile.builder().operation(OPERATION_CREATE).tas("DCA01q").features(createHGFeatures()).build();
	}
	
	private static Profile getResidentialProfile() {
		return Profile.builder().operation(OPERATION_CREATE).tas("DCA01q").features(createResidentialFeatures()).build();
	}

	public static SearchRequest getSearchRequestForPublicIdentity() {

		StringBuilder userIdSB = new StringBuilder("sip:").append("+1").append(PHONE_NUMBER).append("@")
				.append("ims.eng.rr.com");

		return getSearchRequest(userIdSB);
	}

	public static SearchRequest getSearchRequestForPrivateIdentity() {

		StringBuilder userIdSB = new StringBuilder(PHONE_NUMBER).append("@").append("ims.eng.rr.com");
		return getSearchRequest(userIdSB);
	}

	public static SearchRequest getSearchRequest(StringBuilder userIdSB) {

		Alias alias = Alias.builder().name(PUBLIC_SEARCH_NAME).value(userIdSB.toString()).build();
		Base base = Base.builder().objectclass("Subscriber").alias(alias).build();
		return SearchRequest.builder().xsi("http://www.w3.org/2001/XMLSchema-instance")
				.version("HSS_SUBSCRIBER_v82").base(base).build();
	}

	public static SpmlResponse getSpmlResponse() {
		return new SpmlResponse(null, null, null, "13718275614005466511585250035104", getSubscriber(), null, "success", null, "HSS_SUBSCRIBER_v82", null);
	}

	public static SpmlResponse getFailedSpmlResponse() {
		return new SpmlResponse(null, null, null, "13718275614005466511585250035104", getSubscriber(), null, "failure", null, "HSS_SUBSCRIBER_v82", null);
	}
	
	public static AddRequest getAddRequest() {

		return AddRequest.builder()
				
				.version("HSS_SUBSCRIBER_v82")
				.newGenerated("true")
				.language("en_us")
				.subscriber("urn:siemens:names:prov:gw:HSS_SUBSCRIBER:8:2")
				.returnResultingObject("none")
				// Prepare the Subscriber.
				.object(getSubscriber())
				.build();
	}
	
	public static AddRequest getHGAddRequest() {

		return AddRequest.builder()
				
				.version("HSS_SUBSCRIBER_v82")
				.newGenerated("true")
				.language("en_us")
				.subscriber("urn:siemens:names:prov:gw:HSS_SUBSCRIBER:8:2")
				.returnResultingObject("none")
				// Prepare the Subscriber.
				.object(getHGSubscriber())
				.build();
	}
	
	public static ModifyRequest getHGModifyRequest() {

		return ModifyRequest.builder().identifier(SUBSCRIBER_ID).build();
	}
	
	public static ModifyRequest getHGModifyRequestWithModification() {

		return ModifyRequest.builder().identifier(SUBSCRIBER_ID)
				.modification(Stream.of(getPublicIdentityModification()).collect(Collectors.toList())).build();
	}

	public static ModifyRequest getModifyRequestWithModification() {

		return ModifyRequest.builder().identifier(SUBSCRIBER_ID)
				.modification(
						Stream.of(getPublicIdentityModification(), null, null, null, null).collect(Collectors.toList()))
				.build();
	}

	public static String getPublicKeyStringSearchRequest() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
				"<spml:searchRequest xmlns:spml=\"urn:siemens:names:prov:gw:SPML:2:0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n" + 
				"   <version>HSS_SUBSCRIBER_v82</version>\r\n" + 
				"   <base>\r\n" + 
				"      <objectclass>Subscriber</objectclass>\r\n" + 
				"      <alias name=\"impu\" value=\"sip:+18163888611@ims.eng.rr.com\" />\r\n" + 
				"   </base>\r\n" + 
				"</spml:searchRequest>";
	}
	
	public static String getPublicKeyStringSoapRequest() {
		return 	"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
				"<SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\r\n" + 
				"   <SOAP-ENV:Header />\r\n" + 
				"   <SOAP-ENV:Body>\r\n" + 
				"      <spml:searchRequest xmlns:spml=\"urn:siemens:names:prov:gw:SPML:2:0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n" + 
				"         <version>HSS_SUBSCRIBER_v82</version>\r\n" + 
				"         <base>\r\n" + 
				"            <objectclass>Subscriber</objectclass>\r\n" + 
				"            <alias name=\"impu\" value=\"sip:+18216328886@ims.eng.rr.com\" />\r\n" + 
				"         </base>\r\n" + 
				"      </spml:searchRequest>\r\n" + 
				"   </SOAP-ENV:Body>\r\n" + 
				"</SOAP-ENV:Envelope>";
	}
	
	public static String getPrivateKeyStringSearchRequest() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
				"<spml:searchRequest xmlns:spml=\"urn:siemens:names:prov:gw:SPML:2:0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n" + 
				"   <version>HSS_SUBSCRIBER_v82</version>\r\n" + 
				"   <base>\r\n" + 
				"      <objectclass>Subscriber</objectclass>\r\n" + 
				"      <alias name=\"impi\" value=\"219BF751A12481C6@ims.eng.rr.com\" />\r\n" + 
				"   </base>\r\n" + 
				"</spml:searchRequest>";
	}

	public static String getStringModifyResponse() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<spml:modifyResponse xmlns:spml=\"urn:siemens:names:prov:gw:SPML:2:0\" xmlns:subscriber=\"urn:siemens:names:prov:gw:HSS_SUBSCRIBER:8:2\" executionTime=\"33\" language=\"en_us\" requestID=\"-730f14c4:1638663eead:7da4\" result=\"success\">\n" +
				"   <version>HSS_SUBSCRIBER_v82</version>\n" +
				"   <objectclass>Subscriber</objectclass>\n" +
				"   <identifier>14786136245232146014940388441980</identifier>\n" +
				"   <modification name=\"hss/publicUserId\" operation=\"setoradd\" scope=\"uniqueTypeMapping\">\n" +
				"      <match xmlns:ns2=\"urn:siemens:names:prov:gw:HSS_SUBSCRIBER:8:2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:PublicUserId\">\n" +
				"         <originalPublicUserId>sip:mlhg_201864_0003@ims.eng.rr.com</originalPublicUserId>\n" +
				"         <irsId>irs1676394579646706</irsId>\n" +
				"      </match>\n" +
				"      <valueObject xmlns:ns2=\"urn:siemens:names:prov:gw:HSS_SUBSCRIBER:8:2\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns2:PublicUserId\">\n" +
				"         <originalPublicUserId>sip:mlhg_201864_0003@ims.eng.rr.com</originalPublicUserId>\n" +
				"         <defaultIndication>false</defaultIndication>\n" +
				"         <serviceProfileName>sp1676394579646706</serviceProfileName>\n" +
				"         <irsId>irs1676394579646706</irsId>\n" +
				"      </valueObject>\n" +
				"   </modification>\n" +
				"</spml:modifyResponse>";
	}

	public static String getStringSearchResponse() {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
				"<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\r\n" + 
				"   <soapenv:Body>\r\n" + 
				"      <spml:searchResponse xmlns:spml=\"urn:siemens:names:prov:gw:SPML:2:0\" executionTime=\"14\" requestID=\"-782d1f7e:16293c626b7:536\" result=\"success\" searchStatus=\"completeResult\">\r\n" + 
				"         <version>HSS_SUBSCRIBER_v82</version>\r\n" + 
				"         <objects xmlns:ns2=\"urn:siemens:names:prov:gw:HSS_SUBSCRIBER:8:2\" xsi:type=\"ns2:Subscriber\">\r\n" + 
				"            <identifier>13718275614005466511585250035104</identifier>\r\n" + 
				"            <hss>\r\n" + 
				"               <subscriptionId>1</subscriptionId>\r\n" + 
				"               <profileType>normal</profileType>\r\n" + 
				"               <adminBlocked>false</adminBlocked>\r\n" + 
				"               <defaultScscfRequired>true</defaultScscfRequired>\r\n" + 
				"               <ccfPrimary>primaryccf.ims.rr.com</ccfPrimary>\r\n" + 
				"               <ccfSecondary>secondaryccf.ims.rr.com</ccfSecondary>\r\n" + 
				"               <privateUserId>\r\n" + 
				"                  <privateUserId>219BF751A12481C6@ims.eng.rr.com</privateUserId>\r\n" + 
				"                  <httpDigestKey><![CDATA[01623E63117EB61561AD55AE4C072CA4]]></httpDigestKey>\r\n" + 
				"                  <httpDigestKeyVersion>1</httpDigestKeyVersion>\r\n" + 
				"                  <preferredAuthenticationScheme>httpDigest</preferredAuthenticationScheme>\r\n" + 
				"                  <actAsVLR>false</actAsVLR>\r\n" + 
				"                  <preferredDomain>ims</preferredDomain>\r\n" + 
				"                  <looseRoutingIndicationRequired>false</looseRoutingIndicationRequired>\r\n" + 
				"               </privateUserId>\r\n" + 
				"               <implicitRegisteredSet>\r\n" + 
				"                  <irsId>irs1601409354118132</irsId>\r\n" + 
				"                  <registrationStatus>notRegistered</registrationStatus>\r\n" + 
				"                  <authenticationPending>false</authenticationPending>\r\n" + 
				"               </implicitRegisteredSet>\r\n" + 
				"               <publicUserId>\r\n" + 
				"                  <publicUserId>sip:+18163888611@ims.eng.rr.com</publicUserId>\r\n" + 
				"                  <originalPublicUserId>sip:+18163888611@ims.eng.rr.com</originalPublicUserId>\r\n" + 
				"                  <barringIndication>false</barringIndication>\r\n" + 
				"                  <defaultIndication>true</defaultIndication>\r\n" + 
				"                  <serviceProfileName>sp1601409354118132</serviceProfileName>\r\n" + 
				"                  <irsId>irs1601409354118132</irsId>\r\n" + 
				"                  <displayNamePrivacy>false</displayNamePrivacy>\r\n" + 
				"               </publicUserId>\r\n" + 
				"               <publicUserId>\r\n" + 
				"                  <publicUserId>sip:8163888611@ims.eng.rr.com</publicUserId>\r\n" + 
				"                  <originalPublicUserId>sip:8163888611@ims.eng.rr.com</originalPublicUserId>\r\n" + 
				"                  <barringIndication>false</barringIndication>\r\n" + 
				"                  <defaultIndication>false</defaultIndication>\r\n" + 
				"                  <serviceProfileName>sp1601409354118132</serviceProfileName>\r\n" + 
				"                  <irsId>irs1601409354118132</irsId>\r\n" + 
				"                  <displayNamePrivacy>false</displayNamePrivacy>\r\n" + 
				"               </publicUserId>\r\n" + 
				"               <serviceProfile>\r\n" + 
				"                  <profileName>sp1601409354118132</profileName>\r\n" + 
				"                  <globalFilterId>\r\n" + 
				"                     <globalFilterId>900COS</globalFilterId>\r\n" + 
				"                  </globalFilterId>\r\n" + 
				"                  <globalFilterId>\r\n" + 
				"                     <globalFilterId>INTLCOS</globalFilterId>\r\n" + 
				"                  </globalFilterId>\r\n" + 
				"                  <globalFilterId>\r\n" + 
				"                     <globalFilterId>MUT-CTS</globalFilterId>\r\n" + 
				"                  </globalFilterId>\r\n" + 
				"                  <globalFilterId>\r\n" + 
				"                     <globalFilterId>MO-CTS</globalFilterId>\r\n" + 
				"                  </globalFilterId>\r\n" + 
				"                  <globalFilterId>\r\n" + 
				"                     <globalFilterId>CIDBCP</globalFilterId>\r\n" + 
				"                  </globalFilterId>\r\n" + 
				"                  <globalFilterId>\r\n" + 
				"                     <globalFilterId>REG-CTS</globalFilterId>\r\n" + 
				"                  </globalFilterId>\r\n" + 
				"                  <subscribedMediaProfileID>\r\n" + 
				"                     <sessionReleasePolicy>deregisterNoForcedSessionRelease</sessionReleasePolicy>\r\n" + 
				"                     <forkingPolicy>mixedForking</forkingPolicy>\r\n" + 
				"                  </subscribedMediaProfileID>\r\n" + 
				"               </serviceProfile>\r\n" + 
				"            </hss>\r\n" + 
				"         </objects>\r\n" + 
				"      </spml:searchResponse>\r\n" + 
				"   </soapenv:Body>\r\n" + 
				"</soapenv:Envelope>";
	}

	public static SoapMessage getSoapMessage() {

		SoapMessage message = null;
		try {
			message = new SoapMessage();
			message.populateMessageBody(getPublicKeyStringSearchRequest());
			
			message.addProperty("endpoint.url", "http://24.24.98.197:8081/ProvisioningGateway/services/SPMLHssSubscriber82Service");
			message.addProperty("http.content.type", "text/xml");
			message.addProperty("http.method.type", "http.method.post");
			message.addProperty("header.SOAPAction", "");
			
			message.setSoapAction("SoapAction");
			message.setSoapHeader("SoapHeader");
			message.setSoapBody("soapenv:Body");
			
		} catch (SoapServiceException e) {
			e.printStackTrace();
		}
		return message;
	}
	
	public static DeleteRequest getDeleteRequest() {

		return DeleteRequest.builder()
				
				.version("HSS_SUBSCRIBER_v82")
				.deleteScope("all")
				.execution("synchronous")
				.identifier(SUBSCRIBER_ID)
				.build();
	}
}
