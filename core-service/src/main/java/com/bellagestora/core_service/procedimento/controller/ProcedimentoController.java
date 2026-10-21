package com.bellagestora.core_service.procedimento.controller;

import com.bellagestora.core_service.procedimento.dto.ProcedimentoRequestDTO;
import com.bellagestora.core_service.procedimento.dto.ProcedimentoResponseDTO;
import com.bellagestora.core_service.procedimento.model.Procedimento;
import com.bellagestora.core_service.procedimento.repository.ProcedimentoRepository;
import com.bellagestora.core_service.procedimento.services.ProcedimentoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/procedimentos")
public class ProcedimentoController {
    @Autowired
    private ProcedimentoService service;

    @PostMapping
    public ResponseEntity<ProcedimentoResponseDTO> CriarProcedimento(@RequestBody @Valid ProcedimentoRequestDTO requestDTO) {
        ProcedimentoResponseDTO responseDTO = service.criarProcedimento(requestDTO);
        return ResponseEntity.status(201).body(responseDTO);
    }

    @GetMapping
    public ResponseEntity<List<ProcedimentoResponseDTO>> ExibirProcedimentos(){
        List<ProcedimentoResponseDTO> responseDTOS = service.exibirProcedimentos();
        return ResponseEntity.ok(responseDTOS);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcedimentoResponseDTO> PegarProcedimento(@PathVariable Long id){
        return ResponseEntity.ok(service.pegarProcedimento(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProcedimentoResponseDTO> AtualizarProcedimento(@PathVariable Long id, @RequestBody @Valid ProcedimentoRequestDTO requestDTO) {
        return ResponseEntity.ok(service.atualizarProcedimento(id,requestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarProcedimento(@PathVariable Long id) {
        service.deletarProcedimento(id);
        return ResponseEntity.noContent().build();
    }
}
