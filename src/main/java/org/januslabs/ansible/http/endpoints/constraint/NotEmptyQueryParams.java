package org.januslabs.ansible.http.endpoints.constraint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy={Validator.class})
@ReportAsSingleViolation
public @interface NotEmptyQueryParams {

  String message() default "{org.januslabs.ansible.http.endpoints.constraint.NotEmptyQueryParams.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}

