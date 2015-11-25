package com.vedri.mtp.frontend.support.stomp;

import lombok.ToString;

@ToString(callSuper = true)
public class DeleteDestinationEvent extends StompDestinationEvent {

	public DeleteDestinationEvent(String destination) {
		super(destination);
	}
}
