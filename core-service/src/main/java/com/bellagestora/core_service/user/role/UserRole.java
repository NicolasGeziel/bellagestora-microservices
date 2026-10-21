package com.bellagestora.core_service.user.role;

public enum UserRole {
    ADMIN("admin"),
    USER("user");
    private String role;

    UserRole (String role){
        this.role = role;
    }

    String getRole(){
        return role;
    }
}
