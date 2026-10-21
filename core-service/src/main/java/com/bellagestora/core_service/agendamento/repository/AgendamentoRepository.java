package com.bellagestora.core_service.agendamento.repository;

import com.bellagestora.core_service.agendamento.model.Agendamento;
import com.bellagestora.core_service.user.model.UserAbbrevietion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long> {
    List<Agendamento> findAllByuser(UserAbbrevietion usuario);
    List<Agendamento> findAllByProcedimento_user(UserAbbrevietion donoDoProcedimento);
}
