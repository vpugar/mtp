package com.vedri.mtp.frontend.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.broker.AbstractBrokerMessageHandler;
import org.springframework.messaging.simp.broker.SimpleBrokerMessageHandler;

import com.vedri.mtp.frontend.support.stomp.CallbackSubscriptionRegistry;

@Configuration
class WebsocketBrokerConfiguration {

	@Autowired
	private AbstractBrokerMessageHandler simpleBrokerMessageHandler;

	@Bean
	public CallbackSubscriptionRegistry callbackSubscriptionRegistry() {
		final CallbackSubscriptionRegistry callbackSubscriptionRegistry = new CallbackSubscriptionRegistry();
		return callbackSubscriptionRegistry;
	}

	@PostConstruct
	public void init() {
		if (simpleBrokerMessageHandler instanceof SimpleBrokerMessageHandler) {
			((SimpleBrokerMessageHandler) simpleBrokerMessageHandler)
					.setSubscriptionRegistry(callbackSubscriptionRegistry());
		}
		else {
			throw new IllegalStateException("Only SimpleBrokerMessageHandler is supported");
		}
	}
}
