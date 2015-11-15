package com.vedri.mtp.frontend.support.stomp;

public interface DestinationListener {

	void onEvent(NoSessionsForDestinationEvent noSessionsForDestinationEvent);

	void onEvent(NewDestinationEvent newDestinationEvent);
}
