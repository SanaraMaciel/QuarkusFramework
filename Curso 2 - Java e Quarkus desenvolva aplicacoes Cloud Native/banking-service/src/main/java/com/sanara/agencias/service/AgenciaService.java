package com.sanara.agencias.service;

import com.sanara.agencias.domain.Agencia;
import com.sanara.agencias.domain.http.AgenciaHttp;
import com.sanara.agencias.domain.http.SituacaoCadastral;
import com.sanara.agencias.exception.AgenciaNaoAtivaOuNaoEncontradaException;
import com.sanara.agencias.repository.AgenciaRepository;
import com.sanara.agencias.service.http.SituacaoCadastralHttpService;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationScoped
public class AgenciaService {

    private final AgenciaRepository agenciaRepository;
    private final MeterRegistry meterRegistry;

    AgenciaService(AgenciaRepository agenciaRepository, MeterRegistry meterRegistry) {
        this.agenciaRepository = agenciaRepository;
        this.meterRegistry = meterRegistry;
    }

    @RestClient
    SituacaoCadastralHttpService situacaoCadastralHttpService;

    public void cadastrar(Agencia agencia) {
        AgenciaHttp agenciaHttp = situacaoCadastralHttpService.buscarPorCnpj(agencia.getCnpj());
        if(agenciaHttp != null && agenciaHttp.getSituacaoCadastral().equals(SituacaoCadastral.ATIVO)) {
            //adiciona métrica de agencia adicionada
            this.meterRegistry.counter("agencia_adicionada_count").increment();
            Log.info("Agencia com CNPJ " + agencia.getCnpj() + " foi adicionada");
            agenciaRepository.persist(agencia);
        } else {
            Log.info("Agencia com CNPJ " + agencia.getCnpj() + " não ativa ou não encontrada");
            //adiciona métrica de agencia nao adicionada
            this.meterRegistry.counter("agencia_nao_adicionada_count").increment();
            throw new AgenciaNaoAtivaOuNaoEncontradaException();
        }
    }

    public Agencia buscarPorId(Long id) {
        return agenciaRepository.findById(id);
    }

    public void deletar(Long id) {
        Log.info("A agência foi deletada");
        agenciaRepository.deleteById(id);
    }

    public void alterar(Agencia agencia) {
        Log.info("A agência com CNPJ " + agencia.getCnpj() + " foi alterada");
        agenciaRepository.update("nome = ?1, razaoSocial = ?2, cnpj = ?3 where id = ?4", agencia.getNome(), agencia.getRazaoSocial(), agencia.getCnpj(), agencia.getId());
    }
}
