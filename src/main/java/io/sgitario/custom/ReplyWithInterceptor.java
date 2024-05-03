package io.sgitario.custom;

import io.quarkus.arc.AbstractAnnotationLiteral;
import io.quarkus.arc.runtime.InterceptorBindings;
import io.smallrye.faulttolerance.api.FaultTolerance;
import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;
import org.eclipse.microprofile.config.Config;
import org.jboss.logging.Logger;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

@Interceptor
@ReplyWith
@Priority(jakarta.interceptor.Interceptor.Priority.PLATFORM_BEFORE + 1)
public class ReplyWithInterceptor {

    private static final Logger LOGGER = Logger.getLogger(ReplyWithInterceptor.class);

    private final Config config;

    public ReplyWithInterceptor(Config config) {
        this.config = config;
    }

    @AroundInvoke
    public Object intercept(InvocationContext context) throws Exception {
        Optional<ReplyWith> interceptionContext = getArcCacheInterceptionContext(context);
        if (interceptionContext.isEmpty()) {
            return context.proceed();
        }

        var binding = interceptionContext.get();
        var retry = FaultTolerance.create().withRetry();
        getOptionalValue(binding.maxRetries(), Integer.class)
                .ifPresent(retry::maxRetries);
        var duration = getOptionalValue(binding.delay(), Duration.class);
        if (duration.isPresent()) {
            retry.delay(duration.get().toMillis(), ChronoUnit.MILLIS);
        }

        if (isExponentialBackoffStrategySet(binding)) {
            var exponentialBackoff = retry.withExponentialBackoff();
            getOptionalValue(binding.exponentialBackoff().factor(), Integer.class)
                    .ifPresent(exponentialBackoff::factor);
            getOptionalValue(binding.exponentialBackoff().maxDelay(), Duration.class)
                    .ifPresent(maxDelay -> exponentialBackoff.maxDelay(maxDelay.toMillis(), ChronoUnit.MILLIS));
            retry = exponentialBackoff.done();
        }

        return retry.done().build().call(context::proceed);
    }

    private boolean isExponentialBackoffStrategySet(ReplyWith binding) {
        Optional<Boolean> enabled = getOptionalValue(binding.exponentialBackoff().enabled(), Boolean.class);
        if (enabled.isPresent() && Boolean.TRUE.equals(enabled.get())) {
            return true;
        }

        return Stream.of(binding.exponentialBackoff().factor(),
                        binding.exponentialBackoff().maxDelay())
                .filter(this::isNotEmpty)
                .map(config::getConfigValue)
                .anyMatch(c -> isNotEmpty(c.getValue()));
    }

    private <T> Optional<T> getOptionalValue(String property, Class<T> clazz) {
        if (!isNotEmpty(property)) {
            return Optional.empty();
        }

        return config.getOptionalValue(property, clazz);
    }

    private boolean isNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }

    private Optional<ReplyWith> getArcCacheInterceptionContext(
            InvocationContext invocationContext) {
        Set<AbstractAnnotationLiteral> bindings = InterceptorBindings.getInterceptorBindingLiterals(invocationContext);
        if (bindings == null) {
            LOGGER.trace("Interceptor bindings not found in ArC");
            // This should only happen when the interception is not managed by Arc.
            return Optional.empty();
        }

        for (AbstractAnnotationLiteral binding : bindings) {
            if (binding.annotationType().isAssignableFrom((ReplyWith.class))) {
                return Optional.of((ReplyWith) binding);
            }
        }

        return Optional.empty();
    }
}
