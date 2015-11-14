package com.vedri.mtp.frontend.web.websocket.transaction;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Slf4j
@Controller
public class TransactionPeriodicService implements ApplicationListener {

	private final WebsocketSession websocketSession;

	@Autowired
	public TransactionPeriodicService(WebsocketSession websocketSession) {
		this.websocketSession = websocketSession;
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof SessionConnectedEvent) {
			log.debug("SessionConnectedEvent {}", event);
		}
		else if (event instanceof SessionDisconnectEvent) {
			log.debug("SessionConnectedEvent {}", event);
		}
	}
}
