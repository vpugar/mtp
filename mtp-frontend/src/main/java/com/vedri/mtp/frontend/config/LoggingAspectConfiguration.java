package com.vedri.mtp.frontend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import com.vedri.mtp.core.MtpConstants;
import com.vedri.mtp.frontend.support.aop.logging.LoggingAspect;

@Configuration
@EnableAspectJAutoProxy
public class LoggingAspectConfiguration {

	@Bean
	@Profile(MtpConstants.SPRING_PROFILE_DEVELOPMENT)
	public LoggingAspect loggingAspect(Environment env) {
		return new LoggingAspect(env);
	}
}
