package com.bellagestora.core_service.procedimento.repository;

import com.bellagestora.core_service.user.model.UserAbbrevietion;
import com.bellagestora.core_service.procedimento.model.Procedimento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcedimentoRepository extends JpaRepository<Procedimento, Long> {
    List<Procedimento> findAllByuser(UserAbbrevietion usuario);
}
