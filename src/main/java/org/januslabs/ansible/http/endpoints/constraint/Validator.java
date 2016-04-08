package org.januslabs.ansible.http.endpoints.constraint;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;

public class Validator implements ConstraintValidator<NotEmptyQueryParams, UriInfo> {

  private UriInfo uriInfo;
  public Validator(@Context final UriInfo uriInfo)
  {
    this.uriInfo=uriInfo;
  }
  @Override
  public void initialize(NotEmptyQueryParams constraintAnnotation) 
  {
   
  }

  @Override
  public boolean isValid(UriInfo value, ConstraintValidatorContext context) 
  {
    String requestURI= value.getRequestUri().getPath();
    if(requestURI.contains("check")|| (requestURI.contains("hello"))) return true;
    MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
    String key, clusterName, environment;
    key = queryParams.getFirst("key");
    clusterName = queryParams.getFirst("clusterName");
    environment = queryParams.getFirst("env"); 
    if (key == null || (!(key.equalsIgnoreCase("STATUS") || key.equalsIgnoreCase("DEPLOY")
        || key.equalsIgnoreCase("BOUNCE")|| key.equalsIgnoreCase("PING")))) {
      return false;
    }
    if (environment == null) {
      return false;
    }
    if ("STATUS".equalsIgnoreCase(key) && clusterName == null) {
      return false;
    }
    if ("BOUNCE".equalsIgnoreCase(key) && clusterName == null) {
      
    return false;
    }
    return true;
  }

}
