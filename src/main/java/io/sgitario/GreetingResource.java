package io.sgitario;

import io.quarkus.logging.Log;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.InternalServerErrorException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestPath;

@Path("/hello")
public class GreetingResource {

    @GET
    @Path("/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@RestPath String name) {
        Log.info("Handling hello request: " + name);
        if ("jose".equalsIgnoreCase(name)) {
            throw new InternalServerErrorException("Wrong!");
        }

        return "Hello, " + name;
    }
}
