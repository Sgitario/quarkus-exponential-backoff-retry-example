package io.sgitario;

import io.sgitario.custom.ExponentialBackoff;
import io.sgitario.custom.ReplyWith;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "greeting-client")
public interface GreetingClient {
    @Path("/hello/{name}")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @ReplyWith(maxRetries = "my.custom.max-retries",
            delay = "my.custom.delay",
            exponentialBackoff = @ExponentialBackoff(
                    factor = "my.custom.factor",
                    maxDelay = "my.custom.max-delay"
            ))
    String hello(String name);
}
