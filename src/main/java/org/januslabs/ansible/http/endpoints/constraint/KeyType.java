package org.januslabs.ansible.http.endpoints.constraint;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;

@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER,ElementType.METHOD,ElementType.FIELD})
@NotNull
@ReportAsSingleViolation
@Constraint(validatedBy = {KeyTypeValidator.class})
public @interface KeyType {

  String message() default "{org.januslabs.ansible.http.endpoints.constraint.KeyType.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
