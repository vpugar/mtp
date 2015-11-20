package com.vedri.mtp.frontend.support.stomp;

public interface DestinationListener {

	void onEvent(NewDestinationEvent newDestinationEvent);

	void onEvent(DeleteDestinationEvent deleteDestinationEvent);
}
