package org.januslabs.ansible.http.config;

import org.eclipse.jetty.alpn.ALPN;
import org.januslabs.ansible.http.endpoints.AnsibleConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;

import io.undertow.UndertowOptions;

@SpringBootApplication
public class AnsibleHttpApplication {

  public static void main(String[] args) {
    ALPN.debug = true;
    SpringApplication.run(AnsibleHttpApplication.class, args);
  }

  @Bean
  public AnsibleConfiguration ansibleConfiguration() {
    return new AnsibleConfiguration();
  }

  @Bean
  public EmbeddedServletContainerFactory embeddedServletContainerFactory() {

    UndertowEmbeddedServletContainerFactory factory = new UndertowEmbeddedServletContainerFactory();
    factory.addBuilderCustomizers(builder -> {
      builder.setServerOption(UndertowOptions.ENABLE_HTTP2, Boolean.TRUE);
      builder.setServerOption(UndertowOptions.ENABLE_SPDY, Boolean.TRUE);
      builder.setServerOption(UndertowOptions.ENABLE_STATISTICS, Boolean.TRUE);
      builder.setServerOption(UndertowOptions.RECORD_REQUEST_START_TIME, Boolean.TRUE);
      builder.addHttpListener(11081, "localhost");
    });
    return factory;
  }
}
