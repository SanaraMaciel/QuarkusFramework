package com.sanara.agencias.service;

import com.sanara.agencias.domain.Agencia;
import com.sanara.agencias.domain.http.AgenciaHttp;
import com.sanara.agencias.domain.http.SituacaoCadastral;
import com.sanara.agencias.exception.AgenciaNaoAtivaOuNaoEncontradaException;
import com.sanara.agencias.repository.AgenciaRepository;
import com.sanara.agencias.service.http.SituacaoCadastralHttpService;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AgenciaServicePanache {

    @RestClient //anotação necessária para o rest client ser injetado e entendido pelo Quarkus
    SituacaoCadastralHttpService situacaoCadastralHttpService;

    private final List<Agencia> agencias = new ArrayList<>();

    private final AgenciaRepository agenciaRepository;

    public AgenciaServicePanache(AgenciaRepository agenciaRepository) {
        this.agenciaRepository = agenciaRepository;
    }

    public void cadastrar(Agencia agencia) {

        AgenciaHttp agenciaHttp = situacaoCadastralHttpService.buscarPorCnpjPanache(agencia.getCnpj());

        if (agenciaHttp != null && agenciaHttp.getSituacaoCadastral() == SituacaoCadastral.ATIVO) {
            Log.info("Agencia com CNPJ " + agencia.getCnpj() + " foi adicionada");
            agencias.add(agencia);
        } else {
            Log.info("Agencia com CNPJ " + agencia.getCnpj() + " não ativa ou não encontrada");
            throw new AgenciaNaoAtivaOuNaoEncontradaException();
        }
    }

    public Agencia buscarPorId(Integer id) {
        return agencias.stream().filter(agencia -> agencia.getId().equals(id)).toList().getFirst();
    }

    public void deletarPanache(Long id) {
        agencias.removeIf(agencia -> agencia.getId().equals(id));
    }

    public void alterarAnterior(Agencia agencia) {
        deletarPanache(agencia.getId());
        agencias.add(agencia);
    }

    public void alterar(Agencia agencia) {
        agenciaRepository.update(agencia.getNome(), agencia.getRazaoSocial(), agencia.getCnpj(), agencia.getId());
    }
}