package com.bellagestora.auth_service.usuario.dto;

public record UserCreatedEvent(String id, String login, String role) {
}
