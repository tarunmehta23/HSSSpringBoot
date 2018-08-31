package com.charter.provisioning.hss.config;

import com.charter.provisioning.hss.model.Features;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@Component
@ConfigurationProperties(prefix = "hss")
public class HssServiceConfig {
	
	private String sipPrefix;
	
	private String E164DigitPrefix;
	
	private String version;
	
	private String defaultSiteDomain;
	
	private String subscriber;
	
	private String default_subscriber_ns;
	
	private String url;
	
	private String user;
	
	private String password;
	
	private String txId;
	
	private String methodType;
	
	private String contentType;
	
	private String credentialsProvider;
    
	private String contentTypeTextXML;
	
	private String postMethod;
	
	private String soapActionHeader;
	
	private String endPointURL;
	
	private String preferredAuth;
	
	private String preferredDomain;
	
	private String subscriptionId;
	
	private String profileType;
	
	private String adminBlocked;
	
	private String defaultSCSCFRequired;
	
	private String CCFPrimary;
	
	private String CCfSecondary;
	
	private String implicitRegSetPrefix;
	
	private String serviceProfileNamePrefix;
	
	private String sessionReleasePolicy;
	
	private String forkingPolicy;
	
	private String operationCreate;

	private String operationDelete;

	private String operationUpdate;

	private String featureBlock;

	private String hssFilter;

	private String hssPackage;

	private String dPhone;

	private String hGroup;

	private String bGroup;

	private int validFeatureLength;

	@NonNull
	private HashMap<String, String> siteDomain;
	
	private HashMap<String, String> packages;
	
	private List<Features> features;
	
	/**
	 * Method checks for feature package being Residential
	 * 
	 * @param service feature package service to determine the serviceType.
	 * @return boolean
	 */
	public boolean isResidentialService(String service) {
		return (!CollectionUtils.isEmpty(packages) && !StringUtils.isEmpty(packages.get(service))
				&& packages.get(service).matches("Residential"));
	}

	/**
	 * Method checks for feature package being Commercial
	 * 
	 * @param service feature package service to determine the serviceType.
	 * @return boolean
	 */
	public boolean isCommercialService(String service) {
		return (!CollectionUtils.isEmpty(packages) && !StringUtils.isEmpty(packages.get(service))
				&& packages.get(service).matches("Commercial"));
	}

	/**
	 * Method checks for feature package is RCF
	 *
	 * @param service feature package service to determine the serviceType.
	 * @return boolean
	 */
	public boolean isFeaturePackageRCF(String service) {
		return (!CollectionUtils.isEmpty(packages) && !StringUtils.isEmpty(packages.get(service))
				&& packages.get(service).matches("RCF"));
	}

	/**
	 * Retrieves List of features by name from hss-configuration.
	 *
	 * @param name
	 *            feature name to fetch features.
	 * @return List<Features>
	 */
	public List<Features> getFeaturesByName(String name) {
		return features.stream().filter(f -> f.getName().equals(name)).collect(Collectors.toList());
	}
	
	/**
	 * 
	 */
	public HssServiceConfig() {
		super();
	}
	
}
