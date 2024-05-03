package io.sgitario.custom;

import jakarta.enterprise.util.Nonbinding;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface ExponentialBackoff {
    @Nonbinding String enabled() default "";
    @Nonbinding String factor() default "";
    @Nonbinding String maxDelay() default "";
}
