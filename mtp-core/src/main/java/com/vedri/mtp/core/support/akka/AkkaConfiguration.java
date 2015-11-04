package com.vedri.mtp.core.support.akka;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import akka.actor.ActorSystem;
import akka.actor.ExtendedActorSystem;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.vedri.mtp.core.CoreProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AkkaConfiguration {

	@Autowired
	private CoreProperties.Akka akka;

	@Autowired
	private ApplicationContext applicationContext;

	@Autowired
	protected CoreProperties.Cluster clusterInfo;

	/**
	 * Actor SpringExtension singleton for this application.
	 */
	@Bean
	public SpringExtension.SpringExt springExt() {
		final SpringExtension.SpringExt springExt = SpringExtension.SpringExtProvider.get(basicActorSystem());
		springExt.initialize(applicationContext);
		return springExt;
	}

	/**
	 * Actor system singleton for this application.
	 */
	@Bean
	public ExtendedActorSystem actorSystem() {
		return springExt().getSystem();
	}

	@PostConstruct
	public void init() throws Exception {

		ActorSystem system = actorSystem();
		SpringExtension.SpringExt springExt = springExt();

		log.info("Starting root actors on node {}", clusterInfo.getNodeName());

		doCreateRootActors(system, springExt);

		log.info("Started root actors on node {}", clusterInfo.getNodeName());
	}

	@PreDestroy
	public void destroy() {

	}

	@PreDestroy
	public void shutdown() {
		// TODO graceful shutdown of nodes
		log.info("Awaiting actorSystem termination");
		actorSystem().shutdown();
		actorSystem().awaitTermination();
		log.info("actorSystem terminated!");
	}

	protected void doCreateRootActors(final ActorSystem system, final SpringExtension.SpringExt springExt) {
		// init root actors
	}

	private ActorSystem basicActorSystem() {

		final Config defaultConfig = ConfigFactory.load();
		final Config config = defaultConfig.getConfig(clusterInfo.getNodeName()).withFallback(defaultConfig);

		ActorSystem system = ActorSystem.create(akka.getAkkaSystemName(), config);
		// initialize the application context in the Akka Spring Extension

		if (log.isDebugEnabled() && akka.isLogConfiguration()) {
			log.debug(system.settings().toString());
		}

		log.info("Actor system created on node {}", clusterInfo.getNodeName());

		return system;
	}
}
