package com.sanara.agencias.service;

import com.sanara.agencias.domain.Agencia;
import com.sanara.agencias.domain.http.AgenciaHttp;
import com.sanara.agencias.exception.AgenciaNaoAtivaOuNaoEncontradaException;
import com.sanara.agencias.domain.http.SituacaoCadastral;
import com.sanara.agencias.service.http.SituacaoCadastralHttpService;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class AgenciaService {

    @RestClient //anotação necessária para o rest client ser injetado e entendido pelo Quarkus
    SituacaoCadastralHttpService situacaoCadastralHttpService;

    private final List<Agencia> agencias = new ArrayList<>();

    public void cadastrar(Agencia agencia) {
        AgenciaHttp agenciaHttp = situacaoCadastralHttpService.buscarPorCnpj(agencia.getCnpj());
        if (agenciaHttp != null && agenciaHttp.getSituacaoCadastral() == SituacaoCadastral.ATIVO) {
            agencias.add(agencia);
        } else {
            throw new AgenciaNaoAtivaOuNaoEncontradaException();
        }
    }

    public Agencia buscarPorId(Integer id) {
        return agencias.stream().filter(agencia -> agencia.getId().equals(id)).toList().getFirst();
    }

    public void deletar(Integer id) {
        agencias.removeIf(agencia -> agencia.getId().equals(id));
    }

    public void alterar(Agencia agencia) {
        deletar(agencia.getId());
        agencias.add(agencia);
    }
}
