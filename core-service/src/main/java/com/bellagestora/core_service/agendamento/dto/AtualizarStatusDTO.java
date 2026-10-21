package com.bellagestora.core_service.agendamento.dto;

import com.bellagestora.core_service.agendamento.model.StatusAgendamento;
import jakarta.validation.constraints.NotNull;

public record AtualizarStatusDTO(
        @NotNull(message = "O status não pode ser nulo")
        StatusAgendamento status
){ }
