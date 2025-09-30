package com.sanara.agencias.services;

import com.sanara.agencias.domain.Agencia;
import com.sanara.agencias.domain.Endereco;
import com.sanara.agencias.domain.http.AgenciaHttp;
import com.sanara.agencias.exception.AgenciaNaoAtivaOuNaoEncontradaException;
import com.sanara.agencias.repository.AgenciaRepository;
import com.sanara.agencias.service.AgenciaService;
import com.sanara.agencias.service.http.SituacaoCadastralHttpService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

@QuarkusTest
public class AgenciaServiceTest {

    @InjectMock
    @RestClient
    private SituacaoCadastralHttpService situacaoCadastralHttpService;

    @InjectMock
    private AgenciaRepository agenciaRepository;

    @Inject
    private AgenciaService agenciaService;

    @Test
    public void deveNaoCadastrarQuandoClientRetornarNull() {
        Agencia agencia = criarAgencia();
        Mockito.when(situacaoCadastralHttpService.buscarPorCnpj("123")).thenReturn(Uni.createFrom().nullItem());

        //await().indefinitely() -> para fazer com que o metodo "espere" ate finalizar o metodo de cadastro, sem tempo definido

        //Adiciona o código para todoh o contexto reativo
        Vertx.vertx().runOnContext(r ->
        {
            Assertions.assertThrows(AgenciaNaoAtivaOuNaoEncontradaException.class, () ->
                    agenciaService.cadastrar(agencia).await().indefinitely());
            Mockito.verify(agenciaRepository, Mockito.never()).persist(agencia);
        });

    }

    @Test
    public void deveCadastrarQuandoClientRetornarSituacaoCadastralAtivo() {
        Agencia agencia = criarAgencia();

        Mockito.when(situacaoCadastralHttpService.buscarPorCnpj("123")).thenReturn(criarAgenciaHttp());
        Vertx.vertx().runOnContext(r -> {
            agenciaService.cadastrar(agencia);
            Mockito.verify(agenciaRepository).persist(agencia);
        });
    }

    private Agencia criarAgencia() {
        Endereco endereco = new Endereco(1, "Rua de teste", "Logradouro de teste", "Complemento de teste", 1);
        return new Agencia(1l, "Agencia Teste", "Razao social da Agencia Teste", "123", endereco);
    }

    private Uni<AgenciaHttp> criarAgenciaHttp() {
        return Uni.createFrom().item(new AgenciaHttp("Agencia Teste", "Razao social da Agencia Teste", "123", "ATIVO"));
    }
}