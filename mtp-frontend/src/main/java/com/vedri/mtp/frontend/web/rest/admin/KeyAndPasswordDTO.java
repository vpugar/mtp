package com.vedri.mtp.frontend.web.rest.admin;

import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeyAndPasswordDTO {

    private String key;
    private String newPassword;

    public KeyAndPasswordDTO() {
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("key", key)
                .add("newPassword", newPassword)
                .toString();
    }
}
