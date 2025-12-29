package com.hammperpulse.auction.annotations;

import com.hammperpulse.auction.customValidations.TimeAfterValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = TimeAfterValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidEndTime {

    String message() default "EndTime must be after start time";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
