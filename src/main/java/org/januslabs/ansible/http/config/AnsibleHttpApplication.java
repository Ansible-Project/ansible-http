package org.januslabs.ansible.http.config;

import org.januslabs.ansible.http.endpoints.AnsibleConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.undertow.UndertowEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.DependsOn;

import io.undertow.UndertowOptions;

@SpringBootApplication
public class AnsibleHttpApplication {

  public static void main(String[] args) {
    SpringApplication.run(AnsibleHttpApplication.class, args);
  }

  @Bean
  public AnsibleConfiguration ansibleConfiguration() {
    return new AnsibleConfiguration();
  }

  /*
      
   */
  @Bean
  @DependsOn(value="ansibleConfiguration")
  public EmbeddedServletContainerFactory embeddedServletContainerFactory() {
   
    UndertowEmbeddedServletContainerFactory factory = new UndertowEmbeddedServletContainerFactory();
   /* factory.setAccessLogDirectory(new File("."));
    factory.setAccessLogEnabled(true);
    factory.setAccessLogPattern(
        "%I %q %m %h %a %l %u %t \"%r\" %s %b (%D ms) %U \"%{i,Referer}\" \"%{i,Host}\" \"%{i,User-Agent}\" \"%{o,Content-Type}\" \"%{o,Content-Length}\"");
    factory.setBufferSize(16000);
    factory.setBuffersPerRegion(20);
    factory.setDirectBuffers(true);
    factory.setIoThreads(10);
    factory.setWorkerThreads(100);*/
    factory.addBuilderCustomizers(
        builder -> builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true),
        builder -> builder.setServerOption(UndertowOptions.ENABLE_STATISTICS, true),
        builder -> builder.setServerOption(UndertowOptions.RECORD_REQUEST_START_TIME, true),
        builder -> builder.addHttpListener(11080, "localhost"));

    return factory;
  }
}
