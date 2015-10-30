package com.vedri.mtp.core.support.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.SimpleCommandLinePropertySource;

import com.vedri.mtp.core.MtpConstants;

@Slf4j
public abstract class AbstractApplication {

	static boolean showInfo = false;

	private SpringApplication springApplication;

	/**
	 * If no profile has been configured, set by default the "dev" profile.
	 */
	public static void addDefaultProfile(SpringApplication app, SimpleCommandLinePropertySource source,
			String... customProfiles) {
		if (!source.containsProperty("spring.profiles.active") &&
				!System.getenv().containsKey("SPRING_PROFILES_ACTIVE")) {

			if (customProfiles != null) {

				final List<String> profiles = new ArrayList<>(Arrays.asList(customProfiles));
				profiles.add(MtpConstants.SPRING_PROFILE_DEVELOPMENT);
				app.setAdditionalProfiles(profiles.toArray(new String[profiles.size()]));
			}
			else {
				app.setAdditionalProfiles(MtpConstants.SPRING_PROFILE_DEVELOPMENT);
			}
		}
		else {
			if (customProfiles != null) {
				app.setAdditionalProfiles(customProfiles);
			}
		}
	}

	public ApplicationContext startApplication(String[] args, String... customProfile) throws Exception {

		final SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);
		springApplication = new SpringApplication(getConfigs());

		springApplication.setShowBanner(false);
		addDefaultProfile(springApplication, source, customProfile);

		final ConfigurableApplicationContext applicationContext = springApplication.run(args);
		showApplicationInfo(applicationContext);
		doStart(applicationContext);

		return applicationContext;
	}

	public static void stopApplication(ApplicationContext applicationContext) {
		SpringApplication.exit(applicationContext);
	}

	protected abstract Class[] getConfigs();

	protected abstract void doStart(ApplicationContext context) throws Exception;

	private void showApplicationInfo(ConfigurableApplicationContext applicationContext) {
		if (showInfo) {
			log.info("The beans provided by Spring Boot:");

			String[] beanNames = applicationContext.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				log.info(beanName);
			}
		}
	}

}
