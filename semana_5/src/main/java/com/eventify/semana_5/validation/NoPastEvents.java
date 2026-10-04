package com.eventify.semana_5.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = NoPastEventsValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoPastEvents {

    String message() default "La fecha del evento no puede ser anterior a la fecha actual";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}