package org.januslabs.ansible.http.config;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.januslabs.ansible.http.endpoints.AnsibleEndpoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.SerializationFeature;

@Component
public class AnsibleHttpJerseyConfig extends ResourceConfig {

  public AnsibleHttpJerseyConfig() {
    register(AnsibleEndpoint.class);
    register(LoggingFeature.class);
    property(SerializationFeature.INDENT_OUTPUT.name(), true);
    property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
  }
}
