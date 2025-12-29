package com.hammperpulse.auction.customValidations;

import com.hammperpulse.auction.annotations.ValidUser;
import com.hammperpulse.auction.service.UserService;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidUserValidator implements ConstraintValidator<ValidUser,Integer> {
    @Autowired
    private UserService userService;

    @Override
    public void initialize(ValidUser constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Integer userId, ConstraintValidatorContext constraintValidatorContext) {
        if(userId==null)
            return true;
        return userService.isvalidUser(userId);
    }
}
