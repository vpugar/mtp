package com.vedri.mtp.frontend.support.stomp;

public class NewDestinationEvent extends StompDestinationEvent {

    public NewDestinationEvent(String destination) {
        super(destination);
    }
}
