package com.bellagestora.core_service.agendamento.model;
import com.bellagestora.core_service.procedimento.model.Procedimento;
import com.bellagestora.core_service.user.model.UserAbbrevietion;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name="Agendamentos")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "procedimento_id", nullable = false)
    private Procedimento procedimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserAbbrevietion user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusAgendamento status;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime horario;

    @Column(nullable = false)
    private String nomeCliente;

    @Column(nullable = false)
    private BigDecimal valorCobrado;

    @Column(nullable = false)
    private String email;

    public Agendamento (Procedimento procedimento, LocalDate data, LocalTime horario, String nomeCliente, BigDecimal valorCobrado, String email){
        this.procedimento = procedimento;
        this.data = data;
        this.horario = horario;
        this.nomeCliente = nomeCliente;
        this.valorCobrado = valorCobrado;
        this.email = email;
    }

}
