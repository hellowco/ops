package kr.co.proten.llmops.core.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidNumberValidator implements ConstraintValidator<ValidNumber, String> {

    private ValidNumber.NumberType numberType;

    @Override
    public void initialize(ValidNumber constraintAnnotation) {
        this.numberType = constraintAnnotation.type();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // Allow null or empty strings; use @NotBlank for non-null validation
        }

        try {
            switch (numberType) {
                case INT:
                    Integer.parseInt(value);
                    break;
                case LONG:
                    Long.parseLong(value);
                    break;
                case FLOAT:
                    Float.parseFloat(value);
                    break;
                case DOUBLE:
                    Double.parseDouble(value);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported number type: " + numberType);
            }
            return true;
        } catch (NumberFormatException e) {
            return false; // Invalid number format
        }
    }
}