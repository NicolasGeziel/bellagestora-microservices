package com.bellagestora.core_service.user.repository;

import com.bellagestora.core_service.user.model.UserAbbrevietion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.userdetails.UserDetails;

public interface UserAbbrevietionRepository extends JpaRepository<UserAbbrevietion, String> {
    UserDetails findByLogin(String login);
}
