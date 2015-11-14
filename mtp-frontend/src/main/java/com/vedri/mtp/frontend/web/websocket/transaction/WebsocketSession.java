package com.vedri.mtp.frontend.web.websocket.transaction;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;
import org.springframework.web.socket.messaging.SessionUnsubscribeEvent;

@Slf4j
@Component
public class WebsocketSession implements ApplicationListener {

    private final SimpMessagingTemplate template;

	@Autowired
    public WebsocketSession(SimpMessagingTemplate template) {
        this.template = template;
    }

    @PostConstruct
	public void init() {
		log.info("Init websocket connection");
	}

	@PreDestroy
	public void destroy() {
		log.info("Destroy websocket connection");
	}

	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (event instanceof SessionSubscribeEvent) {
			log.debug("SessionSubscribeEvent {}", event);
		}
		else if (event instanceof SessionUnsubscribeEvent) {
			log.debug("SessionUnsubscribeEvent {}", event);
		}
	}

    public void send(String topic, Object data) {
        template.convertAndSend(topic, data);
    }
}
