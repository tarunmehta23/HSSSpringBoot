package com.charter.provisioning.hss;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.charter.health.HealthStatServlet;
import com.charter.health.HealthStatServletBuilder;

@SpringBootApplication
public class HSSApplication {

	public static void main(String[] args) {

		new SpringApplicationBuilder(HSSApplication.class)
				.properties("spring.config.name:application,spml-config,hss-config",
						"spring.config.location:classpath:/,classpath:/,classpath:/")
				.build().run(args);
	}

	@Bean
	public ServletRegistrationBean<HealthStatServlet> healthServlet(ApplicationContext applicationContext) {
		HealthStatServlet servlet = HealthStatServletBuilder.create("hss").withManifestDetails(HSSApplication.class)
				.build();
		return new ServletRegistrationBean<>(servlet, "/health");
	}

}
