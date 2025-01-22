package kr.co.proten.llmops.core.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidNumber {

    String message() default "Invalid number format";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    NumberType type(); // 숫자 타입 설정

    enum NumberType {
        INT, LONG, FLOAT, DOUBLE
    }
}