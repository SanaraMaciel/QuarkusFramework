package com.sanara.agencias.service.http;

import com.sanara.agencias.domain.http.AgenciaHttp;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * Rest Client para acessar a API banking-validation
 */
@Path("/situacao-cadastral")
@RegisterRestClient(configKey = "situacao-cadastral-api")
public interface SituacaoCadastralHttpService {

    @GET
    @Path("{cnpj}")
    //O Uni promete uma promessa de retorno de valor
    Uni<AgenciaHttp> buscarPorCnpj(String cnpj);
}
