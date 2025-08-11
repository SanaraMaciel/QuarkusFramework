package br.com.sanara.resource;

import br.com.sanara.model.Bitcoin;
import br.com.sanara.service.BitcoinService;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;

@Path("/bitcoins")
public class BitcoinResource {

    @Inject //injeta o recurso
    @RestClient // informa que é um cliente rest
    BitcoinService bitcoinService;

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Bitcoin> listar(){
        return bitcoinService.listar();
    }

}
