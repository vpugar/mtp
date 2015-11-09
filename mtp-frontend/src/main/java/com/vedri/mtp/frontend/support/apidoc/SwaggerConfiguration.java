package com.vedri.mtp.frontend.support.apidoc;

import static springfox.documentation.builders.PathSelectors.regex;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;

import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import com.vedri.mtp.core.MtpConstants;
import com.vedri.mtp.frontend.FrontendProperties;

/**
 * Springfox Swagger configuration.
 *
 * Warning! When having a lot of REST endpoints, Springfox can become a performance issue. In that
 * case, you can use a specific Spring profile for this class, so that only front-end developers
 * have access to the Swagger view.
 */
@Slf4j
@Configuration
@EnableSwagger2
@Profile("!" + MtpConstants.SPRING_PROFILE_PRODUCTION)
public class SwaggerConfiguration {

	public static final String DEFAULT_INCLUDE_PATTERN = "/api/.*";

	/**
	 * Swagger Springfox configuration.
	 */
	@Bean
	public Docket swaggerSpringfoxDocket(FrontendProperties frontendProperties) {
		log.debug("Starting Swagger");
		StopWatch watch = new StopWatch();
		watch.start();
		ApiInfo apiInfo = new ApiInfo(
				frontendProperties.getSwagger().getTitle(),
				frontendProperties.getSwagger().getDescription(),
				frontendProperties.getSwagger().getVersion(),
				frontendProperties.getSwagger().getTermsOfServiceUrl(),
				frontendProperties.getSwagger().getContact(),
				frontendProperties.getSwagger().getLicense(),
				frontendProperties.getSwagger().getLicenseUrl());

		Docket docket = new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo)
				.genericModelSubstitutes(ResponseEntity.class)
				.forCodeGeneration(true)
				.genericModelSubstitutes(ResponseEntity.class)
				.directModelSubstitute(java.time.LocalDate.class, String.class)
				.directModelSubstitute(java.time.ZonedDateTime.class, Date.class)
				.directModelSubstitute(java.time.LocalDateTime.class, Date.class)
				.select()
				.paths(regex(DEFAULT_INCLUDE_PATTERN))
				.build();
		watch.stop();
		log.debug("Started Swagger in {} ms", watch.getTotalTimeMillis());
		return docket;
	}
}
