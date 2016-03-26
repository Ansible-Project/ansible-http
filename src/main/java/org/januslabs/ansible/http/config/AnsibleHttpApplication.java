package org.januslabs.ansible.http.config;

import org.januslabs.ansible.http.endpoints.AnsibleConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AnsibleHttpApplication {

  public static void main(String[] args) {
    SpringApplication.run(AnsibleHttpApplication.class, args);
  }

  @Bean
  public AnsibleConfiguration ansibleConfiguration() {
    return new AnsibleConfiguration();
  }
}
