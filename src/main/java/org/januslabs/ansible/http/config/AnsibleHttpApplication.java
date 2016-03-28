package org.januslabs.ansible.http.config;

import java.io.File;

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
    SpringApplication.run(AnsibleHttpApplication.class, args);
  }

  @Bean
  public AnsibleConfiguration ansibleConfiguration() {
    return new AnsibleConfiguration();
  }
  /*
   * server.undertow.accesslog.dir= .
server.undertow.accesslog.enabled=true
server.undertow.accesslog.pattern= %I %q %m %h %a %l %u %t "%r" %s %b (%D ms) %U "%{i,Referer}" "%{i,Host}" "%{i,User-Agent}" "%{o,Content-Type}" "%{o,Content-Length}" 
server.undertow.buffer-size=16000
server.undertow.buffers-per-region=20 
server.undertow.direct-buffers=true
server.undertow.io-threads=10 
server.undertow.worker-threads=100
   */
  @Bean
  public EmbeddedServletContainerFactory embeddedServletContainerFactory() {
    UndertowEmbeddedServletContainerFactory factory=new UndertowEmbeddedServletContainerFactory();
    factory.setAccessLogDirectory(new File(System.getProperty("user.dir")));
    factory.setAccessLogEnabled(true);
    factory.setAccessLogPattern("%I %q %m %h %a %l %u %t \"%r\" %s %b (%D ms) %U \"%{i,Referer}\" \"%{i,Host}\" \"%{i,User-Agent}\" \"%{o,Content-Type}\" \"%{o,Content-Length}\"");
    factory.setBufferSize(16000);
    factory.setBuffersPerRegion(20);
    factory.setDirectBuffers(true);
    factory.setIoThreads(10);
    factory.setWorkerThreads(100);
    factory.addBuilderCustomizers(
        builder -> builder.setServerOption(UndertowOptions.ENABLE_HTTP2, true),
        builder -> builder.setServerOption(UndertowOptions.ENABLE_STATISTICS, true),
        builder -> builder.setServerOption(UndertowOptions.RECORD_REQUEST_START_TIME, true));
         
    return factory;
  }
}
