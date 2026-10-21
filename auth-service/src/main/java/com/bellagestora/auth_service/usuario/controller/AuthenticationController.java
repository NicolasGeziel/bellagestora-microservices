package com.bellagestora.auth_service.usuario.controller;

import com.bellagestora.auth_service.config.security.TokenSerivce;
import com.bellagestora.auth_service.usuario.dto.AuthenticationDTO;
import com.bellagestora.auth_service.usuario.dto.RegisterDTO;
import com.bellagestora.auth_service.usuario.dto.ResponseLoginDTO;
import com.bellagestora.auth_service.usuario.model.User;
import com.bellagestora.auth_service.usuario.repositories.UserRepository;
import com.bellagestora.auth_service.usuario.services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserRepository repository;
    @Autowired
    private TokenSerivce tokenSerivce;
    @Autowired
    private AuthenticationService service;

    @PostMapping("/login")
    public ResponseEntity Login(@RequestBody @Valid AuthenticationDTO data){
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(),data.password());
        var authentication = authenticationManager.authenticate(usernamePassword);
        var token =  tokenSerivce.generateToken((User) authentication.getPrincipal());
        return ResponseEntity.ok(new ResponseLoginDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data){
        service.registerUtil(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
