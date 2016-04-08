package org.januslabs.ansible.http.endpoints;

import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.UriInfo;

import org.springframework.beans.factory.annotation.Autowired;
import org.zeroturnaround.exec.ProcessExecutor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("execute")
public class AnsibleEndpoint {

  private @Autowired AnsibleConfiguration ansibleConfig;


  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response executePingPlaybook(@QueryParam("env") final String environment,
      @QueryParam("key") final String key) throws Exception {
    log.info("executing hello.yml ");

    String inventoryFileName = ansibleConfig.getInventoryName().replaceAll("env", environment);
    ansibleConfig.setInventoryName(inventoryFileName);
    List<String> commands = new ArrayList<String>();
    commands.add(
        ansibleConfig.getAnsibleLocation() + File.separator + ansibleConfig.getPlaybookCommand());
    commands.add("-i");
    commands.add(
        ansibleConfig.getPlaybookLocation() + File.separator + ansibleConfig.getInventoryName());
    commands.add(
        ansibleConfig.getPlaybookLocation() + File.separator + ansibleConfig.getPingPlaybook());
    log.info("executing  commands {} ", commands);
    String processOutput = new ProcessExecutor(commands).readOutput(true).destroyOnExit().execute()
        .getOutput().getUTF8();

    ExecutionStatus status = new ExecutionStatus();
    if (processOutput != null && processOutput.contains("ERROR"))
      status.setCode(-1);
    else
      status.setCode(0);
    status.setOutput(processOutput);
    log.info("execution result {}  ", processOutput);
    log.debug("execution status {}  ", status);

    return Response.ok().entity(status).status(Status.OK).type(MediaType.APPLICATION_JSON).build();
  }

  @Path("/hello")
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public Response helloworld() throws Exception {
    return Response.ok().entity("HelloWorld!!").status(Status.OK).type(MediaType.TEXT_PLAIN)
        .build();

  }

  @POST
  @Produces(MediaType.APPLICATION_JSON)
  public Response executeCommands(@Context UriInfo uriInfo) throws Exception {

    String key, groupId, version, name, clusterName, environment;
    MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
    key = queryParams.getFirst("key");
    groupId = queryParams.getFirst("groupId");
    version = queryParams.getFirst("version");
    name = queryParams.getFirst("name");
    clusterName = queryParams.getFirst("clusterName");
    environment = queryParams.getFirst("env");


    if (key == null || (!(key.equalsIgnoreCase("STATUS") || key.equalsIgnoreCase("DEPLOY")
        || key.equalsIgnoreCase("BOUNCE")))) {
      ExecutionStatus status = new ExecutionStatus();
      status.setCode(Status.BAD_REQUEST.getStatusCode());
      status.setOutput(
          "Key query param is required, possible values are STATUS, DEPLOY,BOUNCE, PING etc. Example ?key=STATUS");
      return Response.status(Status.BAD_REQUEST).entity(status).type(MediaType.APPLICATION_JSON)
          .build();
    }

    if (environment == null) {
      ExecutionStatus status = new ExecutionStatus();
      status.setCode(Status.BAD_REQUEST.getStatusCode());
      status.setOutput(
          "Environment query param is required, possible values are stage, dev etc. Example ?env=stage");
      return Response.status(Status.BAD_REQUEST).entity(status).type(MediaType.APPLICATION_JSON)
          .build();
    }
    if ("STATUS".equalsIgnoreCase(key) && clusterName == null) {
      ExecutionStatus status = new ExecutionStatus();
      status.setCode(Status.BAD_REQUEST.getStatusCode());
      status.setOutput(
          "Cluster Service query param is required, sample values are aebedx. Example ?clusterName=aebedx");
      return Response.status(Status.BAD_REQUEST).entity(status).type(MediaType.APPLICATION_JSON)
          .build();
    }
    if ("BOUNCE".equalsIgnoreCase(key) && clusterName == null) {
      ExecutionStatus status = new ExecutionStatus();
      status.setCode(Status.BAD_REQUEST.getStatusCode());
      status.setOutput(
          "Cluster Service query param is required, sample values are aebedx. Example ?clusterName=aebedx");
      return Response.status(Status.BAD_REQUEST).entity(status).type(MediaType.APPLICATION_JSON)
          .build();
    }

    String inventoryFileName = ansibleConfig.getInventoryName().replaceAll("env", environment);
    ansibleConfig.setInventoryName(inventoryFileName);
    switch (key.toUpperCase()) {
      case "STATUS":
        return executeStatusPlaybook(clusterName);
      case "DEPLOY":
        return executeDeployPlaybook(groupId, name, version, clusterName);
      case "BOUNCE":
        return executeBouncePlaybook(clusterName);

      default:
        break;
    }



    return Response.ok().entity(new ExecutionStatus()).status(Status.OK)
        .type(MediaType.APPLICATION_JSON).build();
  }

  private Response executeStatusPlaybook(String clusterName) throws Exception {
    log.info("executing status.yml ");

    List<String> commands = new ArrayList<String>();
    commands.add(
        ansibleConfig.getAnsibleLocation() + File.separator + ansibleConfig.getPlaybookCommand());
    commands.add("-i");
    commands.add(
        ansibleConfig.getPlaybookLocation() + File.separator + ansibleConfig.getInventoryName());

    commands.add("-vvvv");
    commands.add("-e");
    commands.add("tcat_cluster_name=" + clusterName + " token=" + getToken());

    commands.add(ansibleConfig.getPlaybookLocation() + File.separator
        + ansibleConfig.getStatusServerPlaybook());

    log.info("executing  commands {} ", commands);
    String processOutput = new ProcessExecutor(commands).readOutput(true).destroyOnExit().execute()
        .getOutput().getUTF8();
    ExecutionStatus status = new ExecutionStatus();
    if (processOutput != null && processOutput.contains("ERROR"))
      status.setCode(-1);
    else
      status.setCode(0);
    status.setOutput(processOutput);
    log.info("execution result {}  ", processOutput);
    log.debug("execution status {}  ", status);

    return Response.ok().entity(status).status(Status.OK).type(MediaType.APPLICATION_JSON).build();

  }

  private String getToken() throws Exception {
    SecureRandom random = SecureRandom.getInstanceStrong();
    byte salt[] = new byte[20];
    random.nextBytes(salt);
    return Base64.getEncoder().encodeToString(salt);
  }

  private Response executeBouncePlaybook(String clusterName) throws Exception {
    log.info("executing tomcat_restart.yml ");

    List<String> commands = new ArrayList<String>();
    commands.add(
        ansibleConfig.getAnsibleLocation() + File.separator + ansibleConfig.getPlaybookCommand());
    commands.add("-i");
    commands.add(
        ansibleConfig.getPlaybookLocation() + File.separator + ansibleConfig.getInventoryName());

    commands.add("-vvvv");
    commands.add("-e");
    commands.add("tcat_cluster_name=" + clusterName + " token=" + getToken());

    commands.add(ansibleConfig.getPlaybookLocation() + File.separator
        + ansibleConfig.getBounceServerPlaybook());

    log.info("executing  commands {} ", commands);
    String processOutput = new ProcessExecutor(commands).readOutput(true).destroyOnExit().execute()
        .getOutput().getUTF8();
    ExecutionStatus status = new ExecutionStatus();
    if (processOutput != null && processOutput.contains("ERROR"))
      status.setCode(-1);
    else
      status.setCode(0);
    status.setOutput(processOutput);
    log.info("execution result {}  ", processOutput);
    log.debug("execution status {}  ", status);

    return Response.ok().entity(status).status(Status.OK).type(MediaType.APPLICATION_JSON).build();

  }

  private Response executeDeployPlaybook(String groupId, String name, String version,
      String clusterName) throws Exception {
    log.info("executing upgrade_war.yml ");
    SecureRandom random = SecureRandom.getInstanceStrong();
    byte salt[] = new byte[20];
    random.nextBytes(salt);
    List<String> commands = new ArrayList<String>();
    commands.add(
        ansibleConfig.getAnsibleLocation() + File.separator + ansibleConfig.getPlaybookCommand());
    commands.add("-i");
    commands.add(
        ansibleConfig.getPlaybookLocation() + File.separator + ansibleConfig.getInventoryName());

    commands.add("-vvvv");
    commands.add("-e");
    commands
        .add("name=" + name + " version=" + version + " groupid=" + groupId + " tcat_cluster_name="
            + clusterName + " token=" + Base64.getEncoder().encodeToString(salt));

    commands.add(ansibleConfig.getPlaybookLocation() + File.separator
        + ansibleConfig.getUpgradeWarPlaybook());

    log.info("executing  commands {} ", commands);
    String processOutput = new ProcessExecutor(commands).readOutput(true).destroyOnExit().execute()
        .getOutput().getUTF8();
    ExecutionStatus status = new ExecutionStatus();
    if (processOutput != null && processOutput.contains("ERROR"))
      status.setCode(-1);
    else
      status.setCode(0);
    status.setOutput(processOutput);
    log.info("execution result {}  ", processOutput);
    log.debug("execution status {}  ", status);

    return Response.ok().entity(status).status(Status.OK).type(MediaType.APPLICATION_JSON).build();

  }

  @Path("/check/{value: .*}")
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public Response passCheck(@javax.ws.rs.PathParam("value") String value) throws Exception {
    log.info("Token {}", value);
    return Response.ok().entity("@n$ible.").status(Status.OK).type(MediaType.TEXT_PLAIN).build();

  }


}
