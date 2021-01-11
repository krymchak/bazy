package com.stellarity.workingTime.repository.entity.enums;

import java.util.HashMap;
import java.util.Map;

public enum TypeOfUser {
    MANAGER("manager"),
    EMPLOYER("employer");

    private String type;

    private static final Map<String, TypeOfUser> stringToTypeMap = new HashMap<String, TypeOfUser>();

    static {
        for (TypeOfUser type : TypeOfUser.values()) {
            stringToTypeMap.put(type.value(), type);
        }
    }

    public static TypeOfUser fromString(String s) {
        TypeOfUser type = stringToTypeMap.get(s);

        return type;
    }

    TypeOfUser(String type) {
        this.type = type;
    }


    public String value() {
        return type;
    }
}