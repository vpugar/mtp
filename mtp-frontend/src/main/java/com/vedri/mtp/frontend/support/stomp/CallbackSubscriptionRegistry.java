package com.vedri.mtp.frontend.support.stomp;

import java.util.Optional;

import lombok.Setter;

import org.springframework.messaging.Message;
import org.springframework.messaging.simp.broker.DefaultSubscriptionRegistry;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

@Setter
@Component
public class CallbackSubscriptionRegistry extends DefaultSubscriptionRegistry {

	private Optional<DestinationListener> listener = Optional.empty();

	@Override
	protected void addSubscriptionInternal(String sessionId, String subsId, String destination, Message<?> message) {

		super.addSubscriptionInternal(sessionId, subsId, destination, message);

        listener.ifPresent(destinationListener -> destinationListener.onEvent(new NewDestinationEvent(destination)));
	}

	@Override
	protected MultiValueMap<String, String> findSubscriptionsInternal(String destination, Message<?> message) {

		final MultiValueMap<String, String> result = super.findSubscriptionsInternal(destination, message);

		if (result.isEmpty()) {
            listener.ifPresent(destinationListener ->
                    destinationListener.onEvent(new NoSessionsForDestinationEvent(destination)));
		}

		return result;
	}

}
