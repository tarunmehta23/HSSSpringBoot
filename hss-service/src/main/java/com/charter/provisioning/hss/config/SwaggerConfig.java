package com.charter.provisioning.hss.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	@Bean
	public Docket vssServiceAPI() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.basePackage("com.charter.provisioning.hss.controller"))
				.paths(PathSelectors.regex("/.*"))
				.build()
				.apiInfo(metaData());
	}

	private ApiInfo metaData() {
		return new ApiInfoBuilder().title("HSS Micro-Service")
				.description("HSS Services")
				.contact(new Contact("network-adapters", "", ""))
				.build();
	}
}
