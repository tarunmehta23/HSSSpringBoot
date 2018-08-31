package com.charter.provisioning.hss.aop;

import com.charter.provisioning.hss.config.HssServiceConfig;
import com.charter.provisioning.hss.exception.ServiceException;
import com.charter.provisioning.hss.model.DigitalPhone;
import com.charter.provisioning.hss.model.Feature;
import com.charter.provisioning.hss.model.PublicIdentity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Slf4j
@Aspect
@Configuration
public class ValidationAspect {

	private HssServiceConfig serviceConfig;

	@Autowired
	public ValidationAspect(HssServiceConfig serviceConfig) {this.serviceConfig = serviceConfig;}

	@Before("execution(* com.charter.provisioning.hss.controller.*.*(..))")
	public void inputValidation(JoinPoint joinPoint) {

		StringBuilder validationError = null;
		// Advice
		for (Object obj : joinPoint.getArgs()) {
			if (obj instanceof DigitalPhone) {
				log.info(" Validating DigitalPhone request.");
				validationError = validateDigitalPhone((DigitalPhone) obj);
			}
		}

		if (!StringUtils.isEmpty(validationError)) {
			log.error(String.format("Bad Request %s", validationError.toString()));
			throw new ServiceException(HttpServletResponse.SC_BAD_REQUEST, String.format("Bad Request,%s", validationError.toString()));
		}
	}

	private StringBuilder validateDigitalPhone(DigitalPhone digitalPhone) {

		StringBuilder validationError = new StringBuilder();

		// Validate PublicUser
		if (!CollectionUtils.isEmpty(digitalPhone.getPublicIdentity())) {
			validationError.append(validateTelephoneNumbers(digitalPhone.getPublicIdentity()));
		} else {
			validationError.append(" Invalid PublicIdentity.");
		}
		// Validate input for create requests.
		if (!StringUtils.isBlank(digitalPhone.getOperation()) && serviceConfig.getOperationCreate().equalsIgnoreCase(digitalPhone.getOperation())) {
			//Validate digital phone Name for Create requests.
			if (StringUtils.isBlank(digitalPhone.getName()))
				validationError.append(" Invalid Digital Phone Name.");
				// Validate featurePackage
			else if (!StringUtils.isBlank(digitalPhone.getName()) && serviceConfig.getDPhone().equalsIgnoreCase(digitalPhone.getName())) {
				if (StringUtils.isBlank(digitalPhone.getFeaturePackage())) {
					validationError.append(" Null/Empty Feature package passed for DPhone request.");
				} else if (null == digitalPhone.getProfile() || CollectionUtils.isEmpty(digitalPhone.getProfile().getFeatures())) {
					validationError.append(" Null/Empty Features passed for DPhone request.");
				} else {
					validationError.append(validateProfileFeatures(digitalPhone.getFeaturePackage(), digitalPhone.getProfile().getFeatures()));
				}
			}
		}
		return validationError;
	}

	private StringBuilder validateTelephoneNumbers(List<PublicIdentity> publicIdentity) {

		StringBuilder validationError = new StringBuilder();

		publicIdentity.forEach(p -> {
			if (null == p) {
				validationError.append(" Null/Empty Public Identity passed in the request.");
			} else if (StringUtils.isEmpty(p.getUserId())) {
				validationError.append(" No phone number passed in the request.");
			}
		});
		return validationError;
	}

	private StringBuilder validateProfileFeatures(String featurePackage, List<Feature> featureList) {

		StringBuilder validationError = new StringBuilder();

		if (serviceConfig.getValidFeatureLength() != featureList.stream().filter(f -> null != f && !StringUtils.isBlank(f.getName())
				&& serviceConfig.getHssPackage().equalsIgnoreCase(f.getName()) && featurePackage.equalsIgnoreCase(f.getValue()))
				.count())
			validationError.append(" FeaturePackage mismatch within featureList.");

		if (serviceConfig.getValidFeatureLength() != featureList.stream().filter(f -> null != f && !StringUtils.isBlank(f.getName())
				&& featurePackage.equalsIgnoreCase(f.getName())).count())
			validationError.append(" Feature mismatch within featureList");

		return validationError;
	}
}
