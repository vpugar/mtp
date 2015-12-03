package com.vedri.mtp.frontend.config;

import com.vedri.mtp.frontend.MtpFrontendConstants;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

import com.vedri.mtp.frontend.web.security.AuthoritiesConstants;

@Configuration
public class WebsocketSecurityConfiguration extends AbstractSecurityWebSocketMessageBrokerConfigurer {

	@Override
	protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
		messages
				// message types other than MESSAGE and SUBSCRIBE
				.nullDestMatcher().authenticated()
				// matches any destination that starts with /rooms/
				.simpDestMatchers(MtpFrontendConstants.wrapTopicDestinationPath("**"))
				.hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER, AuthoritiesConstants.DEMO)
				.simpDestMatchers(MtpFrontendConstants.wrapTopicDestinationPath("**"))
				.authenticated()
				.simpSubscribeDestMatchers(MtpFrontendConstants.wrapTopicDestinationPath("/tracker"))
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.simpSubscribeDestMatchers(MtpFrontendConstants.wrapTopicDestinationPath("/tracker/pt*"))
				.hasAuthority(AuthoritiesConstants.ADMIN)
				.simpSubscribeDestMatchers(MtpFrontendConstants.wrapTopicDestinationPath("/tracker/rt*"))
				.hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER, AuthoritiesConstants.DEMO)
				// (i.e. cannot send messages directly to /topic/, /queue/)
				// (i.e. cannot subscribe to /topic/messages/* to get messages sent to
				// /topic/messages-user<id>)
				.simpTypeMatchers(SimpMessageType.MESSAGE, SimpMessageType.SUBSCRIBE).denyAll()
				// catch all
				.anyMessage().denyAll();
	}

	/**
	 * Disables CSRF for Websockets.
	 */
	@Override
	protected boolean sameOriginDisabled() {
		return true;
	}
}
