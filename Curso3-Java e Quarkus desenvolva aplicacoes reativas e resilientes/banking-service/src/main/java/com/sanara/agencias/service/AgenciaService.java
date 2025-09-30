package com.sanara.agencias.service;

import com.sanara.agencias.domain.Agencia;
import com.sanara.agencias.domain.http.AgenciaHttp;
import com.sanara.agencias.domain.http.SituacaoCadastral;
import com.sanara.agencias.exception.AgenciaNaoAtivaOuNaoEncontradaException;
import com.sanara.agencias.repository.AgenciaRepository;
import com.sanara.agencias.service.http.SituacaoCadastralHttpService;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
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

    public Uni<Void> cadastrar(Agencia agencia) {
        Uni<AgenciaHttp> agenciaHttp = situacaoCadastralHttpService.buscarPorCnpj(agencia.getCnpj());

        return agenciaHttp.onItem().ifNull().failWith(new AgenciaNaoAtivaOuNaoEncontradaException())
                .onItem().transformToUni(item -> persistirSeAtiva(agencia, item));

    }

    private Uni<Void> persistirSeAtiva(Agencia agencia, AgenciaHttp agenciaHttp) {
        if (agenciaHttp.getSituacaoCadastral().equals(SituacaoCadastral.ATIVO)) {
            //adiciona métrica de agencia adicionada
            this.meterRegistry.counter("agencia_adicionada_count").increment();
            Log.info("Agencia com CNPJ " + agencia.getCnpj() + " foi adicionada");
            //replaceWithVoid força a aplicação a retornar um void msm querendo que retorne valor
            return agenciaRepository.persist(agencia).replaceWithVoid();
        } else {
            Log.info("Agencia com CNPJ " + agencia.getCnpj() + " não ativa ou não encontrada");
            //adiciona métrica de agencia nao adicionada
            this.meterRegistry.counter("agencia_nao_adicionada_count").increment();
            //cria um uni que falha com a exception
            return Uni.createFrom().failure(new AgenciaNaoAtivaOuNaoEncontradaException());
        }

    }

    @WithSession //para manter a sessão aberta quando acessa o repositorio
    public Uni<Agencia> buscarPorId(Long id) {
        return agenciaRepository.findById(id);
    }

    @WithTransaction //mantém a transação aberta explicitamente, garantindo que a transação iniciada no controle permaneça aberta até o repository.
    public Uni<Void> deletar(Long id) {
        Log.info("A agência foi deletada");
        return agenciaRepository.deleteById(id).replaceWithVoid();
    }

    @WithTransaction
    public Uni<Void> alterar(Agencia agencia) {
        Log.info("A agência com CNPJ " + agencia.getCnpj() + " foi alterada");
        return agenciaRepository.update("nome = ?1, razaoSocial = ?2, cnpj = ?3 where id = ?4", agencia.getNome(),
                agencia.getRazaoSocial(), agencia.getCnpj(), agencia.getId()).replaceWithVoid();
    }
}
