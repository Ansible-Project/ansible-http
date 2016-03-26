package org.januslabs.ansible.http.config;

import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.server.ResourceConfig;
import org.januslabs.ansible.http.endpoints.AnsibleEndpoint;
import org.springframework.stereotype.Component;

@Component
public class AnsibleHttpJerseyConfig extends ResourceConfig {

  public AnsibleHttpJerseyConfig() {
    register(AnsibleEndpoint.class);
    register(new LoggingFilter());
  }
}
