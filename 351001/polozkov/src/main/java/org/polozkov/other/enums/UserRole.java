package org.polozkov.other.enums;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserRole {

    ADMIN("ADMIN"), CUSTOMER("CUSTOMER");
    private String name;

}
