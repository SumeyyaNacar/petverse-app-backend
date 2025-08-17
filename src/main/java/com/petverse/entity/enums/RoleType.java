package com.petverse.entity.enums;

import lombok.Getter;

@Getter
public enum RoleType {

    ADMIN ("Admin"),
    OWNER ("Owner");

    public final String name;
    RoleType(String name) {
        this.name = name;
    }


}
