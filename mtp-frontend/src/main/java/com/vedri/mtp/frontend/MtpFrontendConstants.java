package com.vedri.mtp.frontend;

public interface MtpFrontendConstants {

    String NAME = "MtpFrontend";
    String CONFIG_PREFIX = "mtp.frontend";

    String TOPIC_DESTINATION_PREFIX = "/topic";
    String WEBSOCKET_PREFIX = "/websocket";

    static String wrapWebsocketPath(String name) {
        return WEBSOCKET_PREFIX + "/" + name;
    }

    static String wrapTopicDestinationPath(String name) {
        return TOPIC_DESTINATION_PREFIX + "/" + name;
    }

}
