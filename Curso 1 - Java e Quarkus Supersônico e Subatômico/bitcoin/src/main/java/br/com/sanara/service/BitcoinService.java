package br.com.sanara.service;

import br.com.sanara.model.Bitcoin;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;

@Path("/bitcoins")
@RegisterRestClient(configKey = "bitcoin-api") //usada para o meu serviço poder ser injetado
public interface BitcoinService {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Bitcoin> listar();


}
