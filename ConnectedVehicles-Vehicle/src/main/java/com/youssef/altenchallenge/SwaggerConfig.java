package com.youssef.altenchallenge;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.Collections;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
	/*
	 * @Bean public Docket api() { return new
	 * Docket(DocumentationType.SWAGGER_2).select().apis(RequestHandlerSelectors.any
	 * ()) .paths(PathSelectors.any()).build(); }
	 */

	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.lazy.email.controller")).paths(PathSelectors.any())
				.build().apiInfo(apiEndPointsInfo()).useDefaultResponseMessages(false);
				
	}

	private ApiInfo apiEndPointsInfo() {
		return new ApiInfoBuilder().title("Connected Vehicles - Vehicle RESTful API").description("Vehicle service for Connected Vehicles platform")
				.contact(new Contact("Youssef Hamza", "", "yousif.kamal@gmail.com"))
				.license("Apache 2.0").licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html").version("1.0.0")
				.build();
	}
	
	private ApiKey apiKey() {
		return new ApiKey("Bearer", "Authorization", "header");
	}
}
