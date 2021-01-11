package com.stellarity.workingTime.repository.entity.enums;

import java.util.HashMap;
import java.util.Map;

public enum StatusOfTime {

    PROGRESS("in progress"),
    ACCEPTED("accepted");

    private static final Map<String, StatusOfTime> stringToTypeMap = new HashMap<String, StatusOfTime>();

    static {
        for (StatusOfTime type : StatusOfTime.values()) {
            stringToTypeMap.put(type.value(), type);
        }
    }

    public static StatusOfTime fromString(String s) {
        StatusOfTime type = stringToTypeMap.get(s);

        return type;
    }

    private String status;

    StatusOfTime(String type) {
        this.status = type;
    }

    public String value() {
        return status;
    }
}
