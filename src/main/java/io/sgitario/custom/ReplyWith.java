package io.sgitario.custom;

import jakarta.enterprise.util.Nonbinding;
import jakarta.interceptor.InterceptorBinding;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@InterceptorBinding
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface ReplyWith {
    @Nonbinding String maxRetries() default "";
    @Nonbinding String delay() default "";
    @Nonbinding ExponentialBackoff exponentialBackoff() default @ExponentialBackoff;
}
