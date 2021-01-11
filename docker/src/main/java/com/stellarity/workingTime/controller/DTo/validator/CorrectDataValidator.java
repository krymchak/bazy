package com.stellarity.workingTime.controller.DTo.validator;

import com.stellarity.workingTime.controller.DTo.TimeDTo;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CorrectDataValidator
        implements ConstraintValidator<CorrectData, TimeDTo> {

    private String field;
    private String fieldMatch;

    public void initialize(CorrectData constraintAnnotation) {
        this.field = constraintAnnotation.field();
        this.fieldMatch = constraintAnnotation.fieldMatch();
    }

    public boolean isValid(TimeDTo time,
                           ConstraintValidatorContext context) {

        if (time.getTimeStart() == null || time.getTimeEnd() == null) {
            return true;
        }
        if (time.getTimeStart().compareTo(time.getTimeEnd()) < 0)
            return true;
        return false;
    }
}