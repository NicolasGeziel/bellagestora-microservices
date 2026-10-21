package com.bellagestora.auth_service.usuario.services;

import com.bellagestora.auth_service.config.rabbitmq.RabbitMQConfig;
import com.bellagestora.auth_service.usuario.dto.RegisterDTO;
import com.bellagestora.auth_service.usuario.dto.UserCreatedEvent;
import com.bellagestora.auth_service.usuario.model.User;
import com.bellagestora.auth_service.usuario.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

@Service
public class AuthenticationService {
    @Autowired
    private RabbitTemplate rabbitTemplate;
    @Autowired
    private UserRepository repository;

    @Transactional
    public void registerUtil(RegisterDTO data){

        if(repository.findByLogin(data.login())!=null) {
            throw new IllegalArgumentException("Já existe um usuario registado com este e-mail.");
        }

        String password = new BCryptPasswordEncoder().encode(data.password());
        User user = new User(data.login(), password, data.role());
        this.repository.save(user);

        UserCreatedEvent event = new UserCreatedEvent(
                user.getId(),
                user.getLogin(),
                user.getRole().name()
        );
        rabbitTemplate.convertAndSend(RabbitMQConfig.QUEUE_USER_CREATED,event);
    }
}
