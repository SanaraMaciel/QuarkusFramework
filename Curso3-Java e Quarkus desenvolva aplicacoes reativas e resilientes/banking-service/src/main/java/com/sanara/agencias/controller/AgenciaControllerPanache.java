package com.sanara.agencias.controller;

import com.sanara.agencias.domain.Agencia;
import com.sanara.agencias.service.AgenciaService;
import com.sanara.agencias.service.AgenciaServicePanache;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/agenciasPanache")
public class AgenciaControllerPanache {

    private final AgenciaServicePanache agenciaService;

    AgenciaControllerPanache(AgenciaServicePanache agenciaService) {
        this.agenciaService = agenciaService;
    }

    /**
     * O RestResponse indica o status http da operação, diferentemente do Response o RestResponse é tipificado,
     * ou seja, ele retorna um RestResponse<Agencia>, nesse caso ele retorna um RestResponse<Void> porque não vamos retornar a agencia
     * apenas o status pra saber se deu certo ou não
     *
     * Ao retornar um 201 (Created), queremos informar o path específico onde foi criado. Por isso, utilizamos @Context
     * para obter o contexto da requisição e a URI de onde veio a solicitação. Nesse caso, criamos uma variável
     * uriInfo do tipo UriInfo.
     *
     * @param agencia
     * @return
     */
    @POST
    public RestResponse<Void> cadastrar(Agencia agencia, @Context UriInfo uriInfo) {
        this.agenciaService.cadastrar(agencia);
        return RestResponse.created(uriInfo.getAbsolutePathBuilder().build());
    }



    @GET
    @Path("{id}")
    public RestResponse<Agencia> buscarPorId(Integer id) {
        Agencia agencia = this.agenciaService.buscarPorId(id);
        return RestResponse.ok(agencia);
    }

    @DELETE
    @Path("{id}")
    public RestResponse<Void> deletar(Long id) {
        this.agenciaService.deletarPanache(id);
        return RestResponse.ok();
    }

    @PUT
    public RestResponse<Void> alterar(Agencia agencia) {
        this.agenciaService.alterar(agencia);
        return RestResponse.ok();
    }
}
