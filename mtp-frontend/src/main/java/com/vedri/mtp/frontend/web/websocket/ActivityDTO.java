package com.vedri.mtp.frontend.web.websocket;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for storing a user's activity.
 */
@Getter
@Setter
public class ActivityDTO {

    private String sessionId;

    private String userLogin;

    private String ipAddress;

    private String page;

    private String time;

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("sessionId", sessionId)
                .add("userLogin", userLogin)
                .add("ipAddress", ipAddress)
                .add("page", page)
                .add("time", time)
                .toString();
    }
}
