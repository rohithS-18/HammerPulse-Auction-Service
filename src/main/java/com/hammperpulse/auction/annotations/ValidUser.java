package com.hammperpulse.auction.annotations;

import com.hammperpulse.auction.customValidations.ValidUserValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy =ValidUserValidator.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
public @interface ValidUser {
    String message() default "Seller must be a valid user";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
