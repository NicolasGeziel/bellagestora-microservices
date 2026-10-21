package com.bellagestora.core_service.agendamento.dto;

import com.bellagestora.core_service.agendamento.model.StatusAgendamento;
import com.bellagestora.core_service.procedimento.dto.ProcedimentoResponseDTO;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record AgendamentoResponseDTO(
        @NotNull
        Long agendamento_id,
        @NotNull
        @Future
        LocalDate data,
        @NotNull
        LocalTime horario,
        @NotBlank
        String nomeCliente,
        ProcedimentoResponseDTO procedimentoDTO,
        StatusAgendamento statusAgendamento,
        String nomeProfissional,
        @Email
        String email
){
}
