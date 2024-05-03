package io.sgitario;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.RestPath;

@Path("/client")
public class RestClientResource {

    @RestClient
    GreetingClient client;

    @GET
    @Path("/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String helloViaClient(@RestPath String name) {
        return "From client: " + client.hello(name);
    }
}
