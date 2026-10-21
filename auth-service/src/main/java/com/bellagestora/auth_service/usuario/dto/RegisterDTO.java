package com.bellagestora.auth_service.usuario.dto;

import com.bellagestora.auth_service.usuario.role.UserRole;

public record RegisterDTO (String login, String password, UserRole role) {
}
