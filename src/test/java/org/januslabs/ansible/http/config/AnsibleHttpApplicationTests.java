package org.januslabs.ansible.http.config;

import java.nio.charset.Charset;
import java.util.Arrays;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = AnsibleHttpApplication.class)
@WebIntegrationTest(randomPort = true)
public class AnsibleHttpApplicationTests {
  
  @Value("${local.server.port}")
  private int port;
  @Value("${server.context-path}")
  private String contextRoot;
  @Value("${spring.jersey.application-path}")
  private String jerseycontextRoot;
  private RestTemplate restTemplate = new TestRestTemplate();
	@Test
	public void contextLoads() {
	 
	  restTemplate.getMessageConverters()
      .add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
	  HttpHeaders headers = new HttpHeaders();
      headers.setAccept(Arrays.asList(MediaType.TEXT_PLAIN));
      
      HttpEntity<String> entity = new HttpEntity<String>(headers);
	  ResponseEntity<Response> response = restTemplate.exchange(
          "http://localhost:" + this.port + this.contextRoot +  this.jerseycontextRoot +"/execute",HttpMethod.GET, entity,
          Response.class);
	  Assert.assertEquals( HttpStatus.OK,response.getStatusCode());
	 
	 
	}

}
