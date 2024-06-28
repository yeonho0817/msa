package com.msa.member.data.enumerate;

import lombok.Getter;

@Getter
public enum Permission {
    USER("USER"),
    ADMIN("ADMIN"),
    ;

    private final String description;

    Permission(String description) {
        this.description = description;
    }
}
