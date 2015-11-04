package com.vedri.mtp.frontend.web;

import com.vedri.mtp.frontend.FrontendConfig;
import org.springframework.boot.Banner;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

/**
 * This is a helper Java class that provides an alternative to creating a web.xml.
 */
public class ApplicationWebXml extends SpringBootServletInitializer {

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.bannerMode(Banner.Mode.OFF).sources(FrontendConfig.class);
	}
}
