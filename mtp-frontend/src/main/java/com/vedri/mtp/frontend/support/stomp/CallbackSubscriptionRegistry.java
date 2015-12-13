package com.vedri.mtp.frontend.support.stomp;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import lombok.Setter;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Setter
@Component
public class CallbackSubscriptionRegistry extends DefaultSubscriptionRegistry {

	private Optional<DestinationListener> listener = Optional.empty();

	@Override
	protected void addSubscriptionInternal(String sessionId, String subsId, String destination, Message<?> message) {

		super.addSubscriptionInternal(sessionId, subsId, destination, message);

		final String username = SimpMessageHeaderAccessor.getUser(message.getHeaders()).getName();
		listener.ifPresent(destinationListener -> destinationListener.onEvent(
				new NewDestinationEvent(destination, username)));
	}

	@Override
	protected void removeSubscriptionInternal(String sessionId, String subsId, Message<?> message) {
		SessionSubscriptionInfo info = this.subscriptionRegistry.getSubscriptions(sessionId);
		if (info != null) {
			String destination = info.removeSubscription(subsId);
			if (destination != null) {
				super.removeSubscriptionInternal(sessionId, subsId, message);

				findSubscriptionsInternal(destination, message);
			}
		}
	}

	@Override
	public void unregisterAllSubscriptions(String sessionId) {
		SessionSubscriptionInfo info = this.subscriptionRegistry.getSubscriptions(sessionId);
		if (info != null) {
			final Set<String> destinations = new HashSet<>(info.getDestinations());

			super.unregisterAllSubscriptions(sessionId);

			for (String destination : destinations) {
				findSubscriptionsInternal(destination, null);
			}
		}
	}

	@Override
	protected MultiValueMap<String, String> findSubscriptionsInternal(String destination, Message<?> message) {

		final MultiValueMap<String, String> result = super.findSubscriptionsInternal(destination, message);

		if (result.isEmpty()) {
            listener.ifPresent(destinationListener ->
                    destinationListener.onEvent(new DeleteDestinationEvent(destination)));
		}

		return result;
	}

}
