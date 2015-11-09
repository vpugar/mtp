package com.vedri.mtp.frontend.web.rest.admin;

import ch.qos.logback.classic.Logger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Objects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoggerDTO {

    private String name;

    private String level;

    public LoggerDTO(Logger logger) {
        this.name = logger.getName();
        this.level = logger.getEffectiveLevel().toString();
    }

    @JsonCreator
    public LoggerDTO() {
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("name", name)
                .add("level", level)
                .toString();
    }
}
