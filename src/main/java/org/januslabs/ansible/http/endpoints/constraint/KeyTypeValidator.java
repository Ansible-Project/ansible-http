package org.januslabs.ansible.http.endpoints.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

public class KeyTypeValidator implements ConstraintValidator<KeyType, String> {

  @Context
  private UriInfo uriInfo;

  @Override
  public void initialize(KeyType constraintAnnotation) {


  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      context.buildConstraintViolationWithTemplate(
          "{Key is null , needs to be one of these values STATUS, BOUNCE, DEPLOY}");
      return false;
    }
    return (value.equalsIgnoreCase("STATUS") || value.equalsIgnoreCase("DEPLOY")
        || value.equalsIgnoreCase("BOUNCE")|| value.equalsIgnoreCase("PING"));
  }

}
