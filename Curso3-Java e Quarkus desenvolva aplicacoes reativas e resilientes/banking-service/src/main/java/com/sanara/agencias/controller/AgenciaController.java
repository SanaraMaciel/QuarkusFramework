package com.sanara.agencias.controller;

import com.sanara.agencias.domain.Agencia;
import com.sanara.agencias.service.AgenciaService;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.faulttolerance.api.RateLimit;
import io.smallrye.mutiny.Uni;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;
import org.jboss.resteasy.reactive.RestResponse;

@Path("/agencias")
public class AgenciaController {

    private final AgenciaService agenciaService;

    AgenciaController(AgenciaService agenciaService) {
        this.agenciaService = agenciaService;
    }

    @POST
    @NonBlocking //
    @WithTransaction
    //mantém a transação aberta explicitamente, garantindo que a transação iniciada no controle permaneça aberta até o repository.
    public Uni<RestResponse<Void>> cadastrar(Agencia agencia, @Context UriInfo uriInfo) {
        return this.agenciaService.cadastrar(agencia)
                .replaceWith(RestResponse.created(uriInfo.getAbsolutePathBuilder().build()));
    }

    @GET
    @Path("{id}")
    @RateLimit(value = 5 , window = 10)
    public Uni<RestResponse<Agencia>> buscarPorId(Long id) {
        // retorno com lamba return this.agenciaService.buscarPorId(id).onItem().transform(agencia -> RestResponse.ok(agencia));
        //retorno com inferencia do Rest direto
        return this.agenciaService.buscarPorId(id).onItem().transform(RestResponse::ok);
    }

    @DELETE
    @Path("{id}")
    @Transactional
    public Uni<RestResponse<Void>> deletar(Long id) {
        return this.agenciaService.deletar(id).replaceWith(RestResponse.ok());
    }

    @PUT
    @Transactional
    public Uni<RestResponse<Void>> alterar(Agencia agencia) {
        return this.agenciaService.alterar(agencia).replaceWith(RestResponse.ok());
    }
}
