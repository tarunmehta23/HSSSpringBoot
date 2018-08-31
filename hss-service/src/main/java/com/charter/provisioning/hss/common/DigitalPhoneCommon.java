package com.charter.provisioning.hss.common;

import com.charter.provisioning.hss.config.HssServiceConfig;
import com.charter.provisioning.hss.config.SpmlConfig;
import com.charter.provisioning.hss.model.*;
import com.charter.provisioning.network.hss.subscriber.spml.AddRequest;
import com.charter.provisioning.network.hss.subscriber.spml.DeleteRequest;
import com.charter.provisioning.network.hss.subscriber.spml.ModifyRequest;
import com.charter.provisioning.network.hss.subscriber.spml.SearchRequest;
import com.charter.provisioning.network.hss.subscriber.spml.schema.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DigitalPhoneCommon {

	private SpmlConfig spmlConfig;

	private ServiceUtil serviceUtil;

	private HssServiceConfig serviceConfig;

	@Autowired
	public DigitalPhoneCommon(SpmlConfig spmlConfig, ServiceUtil serviceUtil, HssServiceConfig serviceConfig) {
		this.spmlConfig = spmlConfig;
		this.serviceUtil = serviceUtil;
		this.serviceConfig = serviceConfig;
	}

	/**
	 * Adds Soap Properties.
	 *
	 * @param soapMessage
	 *            message used to make soap call to HSS subscriber
	 * @return SoapMessage
	 */
	public SoapMessage populateSoapMessageProperties(SoapMessage soapMessage) {

		soapMessage.addProperty(serviceConfig.getUrl(), serviceConfig.getEndPointURL());
		soapMessage.addProperty(serviceConfig.getContentType(), serviceConfig.getContentTypeTextXML());
		soapMessage.addProperty(serviceConfig.getMethodType(), serviceConfig.getPostMethod());
		soapMessage.addProperty(serviceConfig.getSoapActionHeader(), "");

		return soapMessage;
	}

	/**
	 * Creates Search Request Object.
	 *
	 * @param identityType
	 *            Search Key used for lookup in HSS Subscriber.
	 * @param userId
	 *            Search value used for lookup in HSS Subscriber.
	 * @param site
	 *            site determines the search domain.
	 * @param appendE164DigitPrefix
	 *            flag used to append E164 Prefix to the search.
	 * @return SearchRequest
	 */
	public SearchRequest createSearchRequest(String identityType, String userId, String site,
			boolean appendE164DigitPrefix) {

		log.info("Inside createSearchRequest printing identityType: {}", identityType);

		StringBuilder userIdSB = new StringBuilder();
		if (spmlConfig.getPublic_identity_search_name().equalsIgnoreCase(identityType)) {
			userIdSB.append(serviceConfig.getSipPrefix());
			if (appendE164DigitPrefix)
				userIdSB.append(serviceConfig.getE164DigitPrefix());
		}
		userIdSB.append(userId).append("@").append(getSiteDomain(site));

		Base base = Base.builder().objectclass(spmlConfig.getObject_class())
				.alias(Alias.builder().name(identityType).value(userIdSB.toString()).build()).build();

		return SearchRequest.builder().xsi(spmlConfig.getXsi_namespace()).version(serviceConfig.getVersion()).base(base)
				.build();
	}

	/**
	 * Creates Add Request Object.
	 *
	 * @param digitalPhone
	 *            the digital phone attribute will be used to create Add Request for
	 *            HSS Subscriber.
	 * @return createAddRequest
	 */
	public AddRequest createAddRequest(DigitalPhone digitalPhone) {

		return AddRequest.builder()

				.version(serviceConfig.getVersion()).newGenerated(spmlConfig.getNew_generated())
				.language(spmlConfig.getDefault_language()).subscriber(serviceConfig.getSubscriber())
				.returnResultingObject(spmlConfig.getReturn_resulting_object())
				// Prepare the Subscriber.
				.object(createSpmlSubscriber(digitalPhone)).build();
	}

	/**
	 * Creates Delete Request Object.
	 *
	 * @param identifier
	 *            the identifier attribute will be used to create Delete Request for
	 *            HSS Subscriber.
	 * @return DeleteRequest
	 */
	public DeleteRequest createDeleteRequest(String identifier) {

		return DeleteRequest.builder()

				.deleteScope(spmlConfig.getDelete_scope()).execution(spmlConfig.getSynchronous())
				.language(spmlConfig.getDefault_language())
				.returnResultingObject(spmlConfig.getReturn_resulting_object()).version(serviceConfig.getVersion())
				.objectclass(spmlConfig.getObject_class()).identifier(identifier).build();
	}

	/**
	 * Creates Modify Request Object.
	 *
	 * @param identifier
	 *            the identifier attribute will be used to create Modify Request for
	 *            HSS Subscriber.
	 * @return ModifyRequest
	 */
	public ModifyRequest createModifyRequest(String identifier) {

		return ModifyRequest.builder()

				.returnResultingObject(spmlConfig.getReturn_resulting_object())
				.language(spmlConfig.getDefault_language()).subscriber(serviceConfig.getSubscriber())
				.xsi(spmlConfig.getXsi_namespace()).version(serviceConfig.getVersion())
				.objectclass(spmlConfig.getObject_class()).identifier(identifier).build();
	}

	/**
	 * Method Creates Modification Object for Public Identity.
	 *
	 * @param publicIdentity
	 *            publicIdentity contains the telephoneNumber
	 * @param serviceProfileName
	 *            profileName for existing subscriber.
	 * @param irsId
	 *            irsId from existing subscriber.
	 * @param defaultIndication
	 *            defaultIndication provided false for HG
	 * @return Modification
	 */
	public Modification createPublicIdentityModification(PublicIdentity publicIdentity, String serviceProfileName,
			String irsId, String defaultIndication) {

		String originalPublicUserId = String.format("%s%s%s%s", serviceConfig.getSipPrefix(),
				publicIdentity.getUserId(), "@", serviceConfig.getDefaultSiteDomain());

		return Modification.builder().operation(spmlConfig.getSpml_op_setoradd())
				.match(Match.builder().type(spmlConfig.getSubscriber_public_user_id())
						.originalPublicUserId(originalPublicUserId).irsId(irsId).build())
				.valueObject(ValueObject.builder().type(spmlConfig.getSubscriber_public_user_id())
						.originalPublicUserId(originalPublicUserId).defaultIndication(defaultIndication)
						.serviceProfileName(serviceProfileName).irsId(irsId).build())
				.build();
	}

	/**
	 * Method Creates Modification Object to remove Public userId
	 *
	 * @param publicUserId
	 *            publicUserId contains the telephoneNumber and IrsId for digital Phone.
	 * @return Modification
	 */
	public Modification createPublicUserDeleteModification(PublicUserId publicUserId) {

		Match match = Match.builder().type(spmlConfig.getSubscriber_public_user_id()).irsId(publicUserId.getIrsId())
				.originalPublicUserId(publicUserId.getOriginalPublicUserId()).build();

		return Modification.builder().operation(spmlConfig.getSpml_op_remove()).match(match).build();
	}

	/**
	 * Method Creates Modification Object for service profile
	 *
	 * @param profileName
	 *            profile name for public userId.
	 * @return Modification
	 */
	public Modification createServiceProfileModification(String profileName) {

		Match match = Match.builder().type(spmlConfig.getSubscriber_service_profile()).profileName(profileName).build();

		return Modification.builder().operation(spmlConfig.getSpml_op_remove()).match(match).build();
	}

	/**
	 * Method Creates Modification Object for irsId
	 *
	 * @param irsId
	 *            irsId for public userId.
	 * @return Modification
	 */
	public Modification createIRSModification(String irsId) {

		Match match = Match.builder().type(spmlConfig.getSubscriber_imp_reg_dataSet()).irsId(irsId).build();

		return Modification.builder().operation(spmlConfig.getSpml_op_remove()).match(match).build();
	}

	/**
	 * Method Creates Modification Object for PrivateIdentity
	 *
	 * @param privateIdentity
	 *            privateIdentity object for a phone number.
	 * @return Modification
	 */
	public Modification createPrivateIdentityModification(PrivateIdentity privateIdentity) {

		Match match = Match.builder().type(spmlConfig.getSubscriber_private_user_id())
				.privateUserId(
						String.format("%s%s%s", privateIdentity.getUserId(), "@", serviceConfig.getDefaultSiteDomain()))
				.build();

		return Modification.builder().operation(spmlConfig.getSpml_op_remove()).match(match).build();
	}

	/**
	 * Method generates 16 char random key.
	 *
	 * @return String
	 */
	public synchronized String generate16CharRandomKey() {
		return serviceUtil.appendRandom(spmlConfig.getIrs_suffix_length(), false);
	}

	/**
	 * Method generates 32 char random key based on current timestamp.
	 *
	 * @return String
	 */
	public synchronized String generate32CharTimestampRandomKey() {
		return serviceUtil.appendTimestamp(spmlConfig.getSubscriber_id_length(), true);
	}

	/**
	 * Retrieves List of features by name from hss-configuration.
	 *
	 * @param name
	 *            feature name to fetch features.
	 * @return List<Features>
	 */
	public List<Feature> getFeaturesByName(String name) {

		return serviceConfig.getFeaturesByName(name).stream()
				.map(f -> Feature.builder().name(f.getName()).operation(serviceConfig.getOperationCreate()).build())
				.collect(Collectors.toList());
	}

	private Subscriber createSpmlSubscriber(DigitalPhone digitalPhone) {

		return Subscriber.builder()

				.type(spmlConfig.getSubscriber_type()).xsi(spmlConfig.getXsi_namespace())
				.identifier(generate32CharRandomKey())
				// Prepare HSS spml request
				.hss(createSpmlHss(digitalPhone)).build();
	}

	private Hss createSpmlHss(DigitalPhone digitalPhone) {

		Hss hss = Hss.builder()

				.subscriptionId(serviceConfig.getSubscriptionId()).profileType(serviceConfig.getProfileType())
				.adminBlocked(serviceConfig.getAdminBlocked())
				.defaultScscfRequired(serviceConfig.getDefaultSCSCFRequired()).ccfPrimary(serviceConfig.getCCFPrimary())
				.ccfSecondary(serviceConfig.getCCfSecondary()).privateUserId(createPrivateUserIds(digitalPhone))
				.build();

		createPublicUserIds(hss, digitalPhone);
		return hss;
	}

	private void createPublicUserIds(Hss hss, DigitalPhone digitalPhone) {

		if (!CollectionUtils.isEmpty(digitalPhone.getPublicIdentity())) {

			digitalPhone.getPublicIdentity().stream()
					.filter(p -> null != p && serviceConfig.getOperationCreate().equalsIgnoreCase(p.getOperation()))

					.forEach(publicIdentity -> {
						String irsIdSuffix = generate16CharRandomKey();
						ImplicitRegisteredSet implicitRegisteredSet = createImplicitRegisteredSet(irsIdSuffix);
						ServiceProfile serviceProfile = createSpmlServiceProfile(irsIdSuffix, digitalPhone);

						hss.addImplicitRegisteredSet(implicitRegisteredSet);
						hss.addServiceProfile(serviceProfile);

						if (null != digitalPhone.getName()
								&& (serviceConfig.getHGroup().equalsIgnoreCase(digitalPhone.getName())
										|| serviceConfig.getBGroup().equalsIgnoreCase(digitalPhone.getName())))
							hss.addPublicUserId(createNationalPublicUserId(publicIdentity, digitalPhone,
									serviceProfile.getProfileName(), implicitRegisteredSet.getIrsId()));
						else if (serviceConfig.isFeaturePackageRCF(digitalPhone.getFeaturePackage()))
							hss.addPublicUserId(createPublicUserId(publicIdentity, digitalPhone,
									serviceProfile.getProfileName(), implicitRegisteredSet.getIrsId(), true));
						else {
							hss.addPublicUserId(createPublicUserId(publicIdentity, digitalPhone,
									serviceProfile.getProfileName(), implicitRegisteredSet.getIrsId(), false));
							hss.addPublicUserId(createPublicUserId(publicIdentity, digitalPhone,
									serviceProfile.getProfileName(), implicitRegisteredSet.getIrsId(), true));
						}
					});

		} else {
			log.error("Cannot Process request without Public User Id");
		}
	}

	private PublicUserId createPublicUserId(PublicIdentity publicIdentity, DigitalPhone digitalPhone,
			String serviceProfileName, String irsId, boolean isE164Format) {

		boolean defaultIndication = false;
		String featurePackage = digitalPhone.getFeaturePackage();
		StringBuilder originalPublicUserId = new StringBuilder(serviceConfig.getSipPrefix());

		if (serviceConfig.isResidentialService(featurePackage)) {
			defaultIndication = true;
			if (isE164Format) {
				originalPublicUserId.append(serviceConfig.getE164DigitPrefix());
				defaultIndication = false;
			}
		} else if (isE164Format && (serviceConfig.isCommercialService(featurePackage)
				|| serviceConfig.isFeaturePackageRCF(featurePackage))) {
			originalPublicUserId.append(serviceConfig.getE164DigitPrefix());
			defaultIndication = true;
		}

		originalPublicUserId.append(publicIdentity.getUserId());
		originalPublicUserId.append("@");
		originalPublicUserId.append(getSiteDomain(digitalPhone.getSite()));

		return PublicUserId.builder()

				.originalPublicUserId(originalPublicUserId.toString()).barringIndication(Boolean.FALSE.toString())
				.defaultIndication(Boolean.toString(defaultIndication)).serviceProfileName(serviceProfileName)
				.irsId(irsId).build();
	}

	private PublicUserId createNationalPublicUserId(PublicIdentity publicIdentity, DigitalPhone digitalPhone,
			String serviceProfileName, String irsId) {


		return PublicUserId.builder()

				.originalPublicUserId(String.format("%s%s%s%s", serviceConfig.getSipPrefix(),
						publicIdentity.getUserId(), "@", getSiteDomain(digitalPhone.getSite())))
				.barringIndication(Boolean.FALSE.toString()).defaultIndication(Boolean.TRUE.toString())
				.serviceProfileName(serviceProfileName).irsId(irsId).build();
	}

	private List<PrivateUserId> createPrivateUserIds(DigitalPhone digitalPhone) {

		List<PrivateUserId> privateUserIds = null;

		if (!CollectionUtils.isEmpty(digitalPhone.getPrivateIdentity())) {

			digitalPhone.getPrivateIdentity().stream().filter(Objects::nonNull).findFirst()
					.ifPresent(privateIdentity -> privateIdentity.setOperation(serviceConfig.getOperationCreate()));

			privateUserIds = digitalPhone.getPrivateIdentity().stream()

					.filter(p -> null != p && serviceConfig.getOperationCreate().equalsIgnoreCase(p.getOperation()))

					.map(p -> PrivateUserId.builder().httpDigestKey(p.getPassword())
							.preferredDomain(serviceConfig.getPreferredDomain())
							.preferredAuthenticationScheme(serviceConfig.getPreferredAuth())
							.privateUserId(createPrivateUserId(p.getUserId(), digitalPhone.getSite())).build())
					.collect(Collectors.toList());
		}
		return privateUserIds;
	}

	private ServiceProfile createSpmlServiceProfile(String irsIdSuffix, DigitalPhone digitalPhone) {

		return ServiceProfile.builder()

				.profileName(serviceConfig.getServiceProfileNamePrefix().concat(irsIdSuffix))
				.globalFilterId(getGlobalFilterIds(digitalPhone.getFeaturePackage(), digitalPhone.getProfile()))

				.subscribedMediaProfileID(
						SubscribedMediaProfileID.builder().sessionReleasePolicy(serviceConfig.getSessionReleasePolicy())
								.forkingPolicy(serviceConfig.getForkingPolicy()).build())
				.build();
	}

	private List<GlobalFilterId> getGlobalFilterIds(String featurePackage, Profile profile) {

		List<GlobalFilterId> globalFilterIds = new ArrayList<>();

		String append = "";
		if (serviceConfig.isResidentialService(featurePackage))
			append = new StringBuilder("-").append(profile.getTas()).toString();

		// Retrieving feature codes from Features and Blocking codes.
		List<String> featureCodes = new ArrayList<>();

		profile.getFeatures().forEach(code -> {
			if (serviceConfig.getOperationCreate().equals(code.getOperation())) {
				if (serviceConfig.getFeatureBlock().equals(code.getName())) {
					featureCodes.addAll(code.getFeatureProperties().stream()
							.filter(p -> serviceConfig.getOperationCreate().equalsIgnoreCase(p.getOperation()))
							.map(FeatureProperty::getName).collect(Collectors.toList()));
				} else {
					featureCodes.add(code.getName());
				}
			}
		});

		if (!CollectionUtils.isEmpty(featureCodes)) {

			final String tas = append;
			serviceConfig.getFeatures().stream().filter(f -> featureCodes.contains(f.getName())).forEach(f -> {
				// Fix to handle Residential package's features behavior.
				if (serviceConfig.getHssFilter().equalsIgnoreCase(f.getActionName())) {

					f.getActionValue().stream().collect(Collectors.toList()).forEach(
							value -> globalFilterIds.add(GlobalFilterId.builder().globalFilterId(value).build()));
				} else {

					f.getActionValue().stream().collect(Collectors.toList()).forEach(value -> globalFilterIds
							.add(GlobalFilterId.builder().globalFilterId(value.concat(tas)).build()));
				}

			});
		}
		return globalFilterIds;
	}

	private String createPrivateUserId(String userId, String site) {
		return new StringBuilder(userId).append("@").append(getSiteDomain(site)).toString();
	}

	private ImplicitRegisteredSet createImplicitRegisteredSet(String irsIdSuffix) {
		return ImplicitRegisteredSet.builder().irsId(serviceConfig.getImplicitRegSetPrefix().concat(irsIdSuffix))
				.build();
	}

	private String getSiteDomain(String site) {
		return serviceConfig.getSiteDomain().get(site) != null ? serviceConfig.getSiteDomain().get(site)
				: serviceConfig.getDefaultSiteDomain();
	}

	private synchronized String generate32CharRandomKey() {
		return serviceUtil.appendRandom(spmlConfig.getSubscriber_id_length(), false);
	}
}
