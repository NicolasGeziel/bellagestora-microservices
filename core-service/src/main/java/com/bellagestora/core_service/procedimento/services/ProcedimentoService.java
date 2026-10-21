package com.bellagestora.core_service.procedimento.services;

import com.bellagestora.core_service.procedimento.dto.ProcedimentoRequestDTO;
import com.bellagestora.core_service.procedimento.dto.ProcedimentoResponseDTO;
import com.bellagestora.core_service.procedimento.model.Procedimento;
import com.bellagestora.core_service.procedimento.repository.ProcedimentoRepository;
import com.bellagestora.core_service.user.model.UserAbbrevietion;
import com.bellagestora.core_service.user.repository.UserAbbrevietionRepository;
import com.bellagestora.core_service.user.role.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProcedimentoService {
    @Autowired
    private ProcedimentoRepository procedimentoRepository;
    @Autowired
    private UserAbbrevietionRepository repositoryUser;

    public ProcedimentoResponseDTO criarProcedimento(ProcedimentoRequestDTO requestDTO) {
        UserAbbrevietion usuarioLogado = getUsuarioLogado();

        Procedimento procedimento = new Procedimento(
                requestDTO.nome(),
                requestDTO.descricao(),
                requestDTO.observacaoDono(),
                requestDTO.valor()
        );
        procedimento.setUser(usuarioLogado);

        Procedimento salvo = procedimentoRepository.save(procedimento);
        return new ProcedimentoResponseDTO(salvo.getId(), salvo.getNome(), salvo.getDescricao(), salvo.getValor());
    }

    public List<ProcedimentoResponseDTO> exibirProcedimentos() {
        UserAbbrevietion usuarioLogado = getUsuarioLogado();
        List<Procedimento> procedimentos;

        if (usuarioLogado.getRole() == UserRole.ADMIN) {
            procedimentos = procedimentoRepository.findAllByuser(usuarioLogado);
        } else {
            procedimentos = procedimentoRepository.findAll();
        }

        return procedimentos.stream()
                .map(p -> new ProcedimentoResponseDTO(p.getId(),p.getNome(), p.getDescricao(), p.getValor()))
                .toList();
    }

    public ProcedimentoResponseDTO pegarProcedimento(Long id) {
        UserAbbrevietion usuarioLogado = getUsuarioLogado();
        Procedimento procedimento = buscarEValidarDono(id, usuarioLogado);

        return new ProcedimentoResponseDTO(procedimento.getId(), procedimento.getNome(), procedimento.getDescricao(), procedimento.getValor());
    }

    public ProcedimentoResponseDTO atualizarProcedimento(Long id, ProcedimentoRequestDTO requestDTO) {
        UserAbbrevietion usuarioLogado = getUsuarioLogado();
        Procedimento procedimento = buscarEValidarDono(id, usuarioLogado);

        procedimento.setNome(requestDTO.nome());
        procedimento.setDescricao(requestDTO.descricao());
        procedimento.setObservacaoDono(requestDTO.observacaoDono());
        procedimento.setValor(requestDTO.valor());

        Procedimento atualizado = procedimentoRepository.save(procedimento);
        return new ProcedimentoResponseDTO(atualizado.getId(), atualizado.getNome(), atualizado.getDescricao(), atualizado.getValor());
    }

    public void deletarProcedimento(Long id) {
        UserAbbrevietion usuarioLogado = getUsuarioLogado();
        Procedimento procedimento = buscarEValidarDono(id, usuarioLogado);

        procedimentoRepository.delete(procedimento);
    }

    private UserAbbrevietion getUsuarioLogado() {
        String loginUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDetails userDetails = repositoryUser.findByLogin(loginUsuario);
        if (userDetails == null) {
            throw new RuntimeException("Usuário não encontrado no sistema.");
        }
        return (UserAbbrevietion) userDetails;
    }

    private Procedimento buscarEValidarDono(Long procedimentoId, UserAbbrevietion usuarioLogado) {
        Procedimento procedimento = procedimentoRepository.findById(procedimentoId)
                .orElseThrow(() -> new RuntimeException("Procedimento não encontrado."));

        if (!procedimento.getUser().getId().equals(usuarioLogado.getId())) {
            throw new RuntimeException("Acesso Negado: Este procedimento pertence a outra conta.");
        }

        return procedimento;
    }
}
