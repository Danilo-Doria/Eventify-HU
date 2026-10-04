package com.eventify.semana_5.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDateTime;

public class NoPastEventsValidator implements ConstraintValidator<NoPastEvents, LocalDateTime> {

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Dejamos que @NotNull valide la presencia si es requerido
        }
        return !value.isBefore(LocalDateTime.now());
    }
}