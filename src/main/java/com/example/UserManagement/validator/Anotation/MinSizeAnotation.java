package com.example.UserManagement.validator.Anotation;

import com.example.UserManagement.validator.MinSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class MinSizeAnotation implements ConstraintValidator<MinSize,String> {
    private int min;
    @Override
    public void initialize(MinSize constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        min=constraintAnnotation.min();
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s.length()>=min;
    }
}
