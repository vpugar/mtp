package com.vedri.mtp.frontend.web.websocket.transaction;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WebsocketSender {

	private final SimpMessagingTemplate template;

	@Autowired
	public WebsocketSender(SimpMessagingTemplate template) {
		this.template = template;
	}

	public void send(String topic, Object data) {
		template.convertAndSend(topic, data);
	}
}
