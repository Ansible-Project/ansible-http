package org.januslabs.ansible.http.endpoints.constraint;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;

@Retention(RetentionPolicy.RUNTIME)
@ReportAsSingleViolation
@Constraint(validatedBy = {})
public @interface KeyType {

  String message() default "{org.januslabs.ansible.http.endpoints.constraint.KeyType.message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
