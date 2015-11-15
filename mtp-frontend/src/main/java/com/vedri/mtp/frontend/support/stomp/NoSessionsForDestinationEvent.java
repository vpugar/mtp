package com.vedri.mtp.frontend.support.stomp;

public class NoSessionsForDestinationEvent extends StompDestinationEvent {

	public NoSessionsForDestinationEvent(String destination) {
		super(destination);
	}
}
