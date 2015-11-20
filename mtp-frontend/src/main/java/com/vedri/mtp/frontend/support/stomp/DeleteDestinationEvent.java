package com.vedri.mtp.frontend.support.stomp;

public class DeleteDestinationEvent extends StompDestinationEvent {

	public DeleteDestinationEvent(String destination) {
		super(destination);
	}
}
