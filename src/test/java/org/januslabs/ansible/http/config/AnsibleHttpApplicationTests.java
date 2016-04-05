package org.januslabs.ansible.http.config;

import java.util.Arrays;
import java.util.HashMap;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.januslabs.ansible.http.endpoints.ExecutionStatus;
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
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
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
  public void contextLoads() throws Exception {

    SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
        new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build());

    HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();


    ((HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory())
        .setHttpClient(httpClient);
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

    HttpEntity<String> entity = new HttpEntity<String>(headers);
    ResponseEntity<ExecutionStatus> response = restTemplate.exchange(
        "https://localhost:" + this.port + this.contextRoot + this.jerseycontextRoot + "/execute",
        HttpMethod.GET, entity, ExecutionStatus.class);
    Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assert.assertEquals(new Integer(0), response.getBody().getCode());
    Assert.assertNotNull(response.getBody().getOutput());

  }

  @Test
  public void contextLoadsNonSSL() throws Exception {

    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));

    HttpEntity<String> entity = new HttpEntity<String>(headers);
    ResponseEntity<ExecutionStatus> response = restTemplate.exchange(
        "http://localhost:" + 11081 + this.contextRoot + this.jerseycontextRoot + "/execute",
        HttpMethod.GET, entity, ExecutionStatus.class);
    Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assert.assertEquals(new Integer(0), response.getBody().getCode());
    Assert.assertNotNull(response.getBody().getOutput());

  }

  @Test
  public void executeCommands() throws Exception {
    SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
        new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build());

    HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();


    ((HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory())
        .setHttpClient(httpClient);
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
    // headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    HashMap<String, String> postParameters = new HashMap<String, String>();
    postParameters.put("field 1", "value 1");
    postParameters.put("field 2", "value 2");
    postParameters.put("field 2", "value 3");
    HttpEntity<HashMap<String, String>> requestEntity =
        new HttpEntity<HashMap<String, String>>(headers);


    ResponseEntity<ExecutionStatus> response =
        restTemplate.exchange(
            "https://localhost:" + this.port + this.contextRoot + this.jerseycontextRoot
                + "/execute" + "?key=DEPLOY&groupId=service.registration&name=assurantregistrationservice&version=2.1.6&clusterName=aebedx",
            HttpMethod.POST, requestEntity, ExecutionStatus.class);
    Assert.assertEquals(HttpStatus.OK, response.getStatusCode());


  }

}
