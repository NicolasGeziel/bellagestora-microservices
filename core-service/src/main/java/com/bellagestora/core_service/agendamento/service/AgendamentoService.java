package com.bellagestora.core_service.agendamento.service;

import com.bellagestora.core_service.agendamento.dto.AgendamentoRequestDTO;
import com.bellagestora.core_service.agendamento.dto.AgendamentoResponseDTO;
import com.bellagestora.core_service.agendamento.model.Agendamento;
import com.bellagestora.core_service.agendamento.model.StatusAgendamento;
import com.bellagestora.core_service.agendamento.repository.AgendamentoRepository;
import com.bellagestora.core_service.procedimento.dto.ProcedimentoResponseDTO;
import com.bellagestora.core_service.procedimento.model.Procedimento;
import com.bellagestora.core_service.procedimento.repository.ProcedimentoRepository;
import com.bellagestora.core_service.user.model.UserAbbrevietion;
import com.bellagestora.core_service.user.repository.UserAbbrevietionRepository;
import com.bellagestora.core_service.user.role.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;
    private final ProcedimentoRepository procedimentoRepository;
    private final UserAbbrevietionRepository userRepository;

    public AgendamentoResponseDTO criarAgendamento(AgendamentoRequestDTO requestDTO){
        UserAbbrevietion usuarioLogado = getUsuarioLogado();
        Procedimento procedimento = procedimentoRepository.findById(requestDTO.procedimento_id())
                .orElseThrow(() -> new RuntimeException("Procedimento não encontrado"));
        Agendamento agendamento = new Agendamento(
                procedimento,
                requestDTO.data(),
                requestDTO.horario(),
                requestDTO.nomeCliente(),
                procedimento.getValor(),
                requestDTO.email()
        );

        agendamento.setUser(usuarioLogado);

        if (usuarioLogado.getRole() == UserRole.ADMIN) {
            agendamento.setStatus(StatusAgendamento.CONFIRMADO);
        } else {
            agendamento.setStatus(StatusAgendamento.PENDENTE);
        }

        Agendamento salvar = agendamentoRepository.save(agendamento);
        return converterParaDtoSemProcedimento(salvar);
    }

    public List<AgendamentoResponseDTO> buscarAgendamentos() {
        UserAbbrevietion usuarioLogado = getUsuarioLogado();
        List<Agendamento> agendamentos;

        if (usuarioLogado.getRole() == UserRole.ADMIN) {
            agendamentos = agendamentoRepository.findAllByProcedimento_user(usuarioLogado);
        } else {
            agendamentos = agendamentoRepository.findAllByuser(usuarioLogado);
        }

        return agendamentos.stream()
                .map(this::converterParaDtoComProcedimento)
                .toList();
    }

    public AgendamentoResponseDTO buscarPorId(Long id) {
        UserAbbrevietion usuarioLogado = getUsuarioLogado();
        Agendamento agendamento = buscarEValidarDono(id, usuarioLogado);

        return converterParaDtoComProcedimento(agendamento);
    }

    public AgendamentoResponseDTO atualizar(Long id, AgendamentoRequestDTO requestDTO) {
        UserAbbrevietion usuarioLogado = getUsuarioLogado();

        Agendamento agendamentoExistente = buscarEValidarDono(id, usuarioLogado);

        Procedimento procedimento = procedimentoRepository.findById(requestDTO.procedimento_id())
                .orElseThrow(() -> new RuntimeException("Procedimento não encontrado"));

        agendamentoExistente.setProcedimento(procedimento);
        agendamentoExistente.setData(requestDTO.data());
        agendamentoExistente.setHorario(requestDTO.horario());
        agendamentoExistente.setNomeCliente(requestDTO.nomeCliente());
        agendamentoExistente.setValorCobrado(procedimento.getValor());
        agendamentoExistente.setEmail(requestDTO.email());

        Agendamento agendamentoAtualizado = agendamentoRepository.save(agendamentoExistente);

        return converterParaDtoComProcedimento(agendamentoAtualizado);
    }

    public void deletar(Long id) {
        UserAbbrevietion usuarioLogado = getUsuarioLogado();

        Agendamento agendamento = buscarEValidarDono(id, usuarioLogado);
        agendamentoRepository.delete(agendamento);
    }

    public AgendamentoResponseDTO mudarStatus(Long id, StatusAgendamento novoStatus){
        UserAbbrevietion usuarioLogado = getUsuarioLogado();
        if (usuarioLogado.getRole() != UserRole.ADMIN) {
            throw new RuntimeException("Acesso Negado: Apenas administradores podem aprovar ou cancelar agendamentos.");
        }

        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));

        if (!agendamento.getProcedimento().getUser().getId().equals(usuarioLogado.getId())) {
            throw new RuntimeException("Acesso Negado: Este agendamento pertence a outro profissional.");
        }

        agendamento.setStatus(novoStatus);
        Agendamento salvo = agendamentoRepository.save(agendamento);

        return converterParaDtoComProcedimento(salvo);
    }

    private UserAbbrevietion getUsuarioLogado() {
        String loginUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
        UserDetails userDetails = userRepository.findByLogin(loginUsuario);

        if (userDetails == null) {
            throw new RuntimeException("Usuário não encontrado no sistema.");
        }
        return (UserAbbrevietion) userDetails;
    }

    private Agendamento buscarEValidarDono(Long id, UserAbbrevietion usuarioLogado) {
        Agendamento agendamento = agendamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado"));
        boolean isCliente = agendamento.getUser().getId().equals(usuarioLogado.getId());
        boolean isProfissional = agendamento.getProcedimento().getUser().getId().equals(usuarioLogado.getId());

        if (!isCliente && !isProfissional) {
            throw new RuntimeException("Acesso Negado: Você não tem permissão para acessar este agendamento.");
        }
        return agendamento;
    }


    private AgendamentoResponseDTO converterParaDtoSemProcedimento(Agendamento agendamento) {
        String nomeProfissional = agendamento.getProcedimento().getUser().getLogin();
        return new AgendamentoResponseDTO(
                agendamento.getId(),
                agendamento.getData(),
                agendamento.getHorario(),
                agendamento.getNomeCliente(),
                null,
                agendamento.getStatus(),
                nomeProfissional,
                agendamento.getEmail()
        );
    }

    private AgendamentoResponseDTO converterParaDtoComProcedimento(Agendamento agendamento) {
        String nomeProfissional = agendamento.getProcedimento().getUser().getLogin();
        ProcedimentoResponseDTO procDTO = new ProcedimentoResponseDTO(
                agendamento.getProcedimento().getId(),
                agendamento.getProcedimento().getNome(),
                agendamento.getProcedimento().getDescricao(),
                agendamento.getProcedimento().getValor()
        );

        return new AgendamentoResponseDTO(
                agendamento.getId(),
                agendamento.getData(),
                agendamento.getHorario(),
                agendamento.getNomeCliente(),
                procDTO,
                agendamento.getStatus(),
                nomeProfissional,
                agendamento.getEmail()
        );
    }
}