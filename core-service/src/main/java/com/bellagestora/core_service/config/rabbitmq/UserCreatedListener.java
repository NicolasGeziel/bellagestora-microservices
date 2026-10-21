package com.bellagestora.core_service.config.rabbitmq;

import com.bellagestora.core_service.user.dto.UserCreatedEvent;
import com.bellagestora.core_service.user.model.UserAbbrevietion;
import com.bellagestora.core_service.user.repository.UserAbbrevietionRepository;
import com.bellagestora.core_service.user.role.UserRole;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserCreatedListener {

    @Autowired
    private UserAbbrevietionRepository repository;

    @RabbitListener(queues = "user.created.queue")
    public void receberMensagem(UserCreatedEvent event) {
        UserAbbrevietion usuario = new UserAbbrevietion();
        usuario.setId(event.id());
        usuario.setLogin(event.login());
        usuario.setRole(UserRole.valueOf(event.role()));
        repository.save(usuario);
        System.out.println("Usuário replicado com sucesso no core-service: " + event.login());
    }
}