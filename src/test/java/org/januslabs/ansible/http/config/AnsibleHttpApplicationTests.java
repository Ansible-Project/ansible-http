package org.januslabs.ansible.http.config;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
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
    ResponseEntity<ExecutionStatus> response = restTemplate.exchange("https://localhost:"
        + this.port + this.contextRoot + this.jerseycontextRoot + "/execute?env=stage&key=PING",
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
    ResponseEntity<ExecutionStatus> response = restTemplate.exchange("http://localhost:" + 11081
        + this.contextRoot + this.jerseycontextRoot + "/execute?env=stage&key=PING", HttpMethod.GET,
        entity, ExecutionStatus.class);
    Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assert.assertEquals(new Integer(0), response.getBody().getCode());
    Assert.assertNotNull(response.getBody().getOutput());

  }

  @Test
  public void check() throws Exception {

    SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
        new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build());

    HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();


    ((HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory())
        .setHttpClient(httpClient);
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.TEXT_PLAIN));


    HttpEntity<String> entity = new HttpEntity<String>(headers);
    ResponseEntity<String> response =
        restTemplate
            .exchange(
                "https://localhost:" + this.port + this.contextRoot + this.jerseycontextRoot
                    + "/execute/check/" + generateRandomString(),
                HttpMethod.GET, entity, String.class);
    Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
    Assert.assertEquals("@n$ible.", response.getBody());


  }

  private String generateRandomString() throws Exception {
    SecureRandom random = SecureRandom.getInstanceStrong();
    byte salt[] = new byte[50];
    random.nextBytes(salt);
    String encodedString = Base64.getEncoder().encodeToString(salt);
    System.out.println(encodedString);
    return encodedString;
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


    HttpEntity<HashMap<String, String>> requestEntity =
        new HttpEntity<HashMap<String, String>>(headers);


    ResponseEntity<ExecutionStatus> response = restTemplate.exchange(
        "https://localhost:" + this.port + this.contextRoot + this.jerseycontextRoot + "/execute"
            + "?key=DEPLOY&groupId=service.registration&name=assurantregistrationservice&version=2.1.6&clusterName=aebedx&env=stage",
        HttpMethod.POST, requestEntity, ExecutionStatus.class);
    Assert.assertEquals(HttpStatus.OK, response.getStatusCode());


  }

  @Test
  public void executeCommands_Bounce() throws Exception {
    SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
        new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build());

    HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();


    ((HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory())
        .setHttpClient(httpClient);
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));


    HttpEntity<HashMap<String, String>> requestEntity =
        new HttpEntity<HashMap<String, String>>(headers);


    ResponseEntity<ExecutionStatus> response = restTemplate.exchange(
        "https://localhost:" + this.port + this.contextRoot + this.jerseycontextRoot + "/execute"
            + "?key=BOUNCE&groupId=service.registration&name=assurantregistrationservice&version=2.1.6&clusterName=aebedx&env=stage",
        HttpMethod.POST, requestEntity, ExecutionStatus.class);
    Assert.assertEquals(HttpStatus.OK, response.getStatusCode());


  }

  @Test
  public void executeCommands_Status() throws Exception {
    SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
        new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build());

    HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();


    ((HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory())
        .setHttpClient(httpClient);
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));


    HttpEntity<HashMap<String, String>> requestEntity =
        new HttpEntity<HashMap<String, String>>(headers);


    ResponseEntity<ExecutionStatus> response = restTemplate.exchange(
        "https://localhost:" + this.port + this.contextRoot + this.jerseycontextRoot + "/execute"
            + "?key=STATUS&groupId=service.registration&name=assurantregistrationservice&version=2.1.6&clusterName=aebedx&env=stage",
        HttpMethod.POST, requestEntity, ExecutionStatus.class);
    Assert.assertEquals(HttpStatus.OK, response.getStatusCode());


  }

  @Test
  public void executeCommands_NoKey() throws Exception {
    SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
        new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build());

    HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();


    ((HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory())
        .setHttpClient(httpClient);
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));


    HttpEntity<HashMap<String, String>> requestEntity =
        new HttpEntity<HashMap<String, String>>(headers);


    ResponseEntity<ExecutionStatus> response = restTemplate.exchange(
        "https://localhost:" + this.port + this.contextRoot + this.jerseycontextRoot + "/execute",
        HttpMethod.POST, requestEntity, ExecutionStatus.class);
    Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Assert.assertNotNull(response.getBody());

  }

  @Test
  public void executeCommands_InvalidKey() throws Exception {
    SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
        new SSLContextBuilder().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build());

    HttpClient httpClient = HttpClients.custom().setSSLSocketFactory(socketFactory).build();


    ((HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory())
        .setHttpClient(httpClient);
    HttpHeaders headers = new HttpHeaders();
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));


    HttpEntity<HashMap<String, String>> requestEntity =
        new HttpEntity<HashMap<String, String>>(headers);


    ResponseEntity<ExecutionStatus> response =
        restTemplate.exchange(
            "https://localhost:" + this.port + this.contextRoot + this.jerseycontextRoot
                + "/execute?key=test&env=stage",
            HttpMethod.POST, requestEntity, ExecutionStatus.class);
    Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    Assert.assertNotNull(response.getBody());

  }

}
