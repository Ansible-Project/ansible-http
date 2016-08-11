package org.januslabs.ansible.http.endpoints;

import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

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

import org.januslabs.ansible.http.endpoints.constraint.NotEmptyQueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.zeroturnaround.exec.ProcessExecutor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Path("execute")
public class AnsibleEndpoint {

  private @Autowired AnsibleConfiguration ansibleConfig;
  
  private @NotEmptyQueryParams @Context UriInfo uriInfo;
 
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response executePingPlaybook(@QueryParam("env") final String environment,
      @QueryParam("key") final String key) throws Exception {
    log.info("executing hello.yml ");

    String inventoryFileName = ansibleConfig.getInventoryName().replaceAll("env", environment);
    ansibleConfig.setInventoryName(inventoryFileName);
    List<String> commands = new ArrayList<>();
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

    MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
    String key, groupId, version, name, clusterName, environment,appServerName,deployArtifactType;
    key = queryParams.getFirst("key");
    groupId = queryParams.getFirst("groupId");
    version = queryParams.getFirst("version");
    name = queryParams.getFirst("name");
    clusterName = queryParams.getFirst("clusterName");
    environment = queryParams.getFirst("env");
    appServerName = queryParams.getFirst("appServerName");
    deployArtifactType=queryParams.getFirst("type");
    appServerName=getValueOrDefault(appServerName);
    deployArtifactType=getValueOrDefaultType(deployArtifactType);
    log.info("Env {} ", environment);
    log.info("Host Inventory file {} ", ansibleConfig.getInventoryName());
    log.info("Appserver  {} ", appServerName);
    log.info("ansible script {} ", ansibleConfig.getInventoryName());
    String modInventoryFileName = ansibleConfig.getInventoryName()
        .substring(0,ansibleConfig.getInventoryName().indexOf("."));
    
    ansibleConfig.setInventoryName(modInventoryFileName+"."+environment);
    log.info("Host Environment file {} ", ansibleConfig.getInventoryName());
    log.info("Received HTTP request {} ", queryParams);
    switch (key.toUpperCase()) {
      case "STATUS":
        return executeStatusPlaybook(clusterName,appServerName);
      case "DEPLOY":
        return executeDeployPlaybook(groupId, name, version, clusterName,appServerName,deployArtifactType);
      case "BOUNCE":
        return executeBouncePlaybook(clusterName,appServerName);

      default:
        break;
    }

    
    return Response.ok().entity(new ExecutionStatus()).status(Status.OK)
        .type(MediaType.APPLICATION_JSON).build();
  }

  private Response executeStatusPlaybook(String clusterName,String appservername) throws Exception {
    log.info("executing status.yml ");

    List<String> commands = new ArrayList<>();
    commands.add(
        ansibleConfig.getAnsibleLocation() + File.separator + ansibleConfig.getPlaybookCommand());
    commands.add("-i");
    commands.add(
        ansibleConfig.getPlaybookLocation() + File.separator + ansibleConfig.getInventoryName());

    commands.add("-vvvv");
    commands.add("-e");
    commands.add(appservername+"_cluster_name=" + clusterName + " token=" + getToken());

    commands.add(ansibleConfig.getPlaybookLocation() + File.separator+ appservername+"_"
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

  private Response executeBouncePlaybook(String clusterName,String appservername) throws Exception {
    log.info("executing tomcat_restart.yml ");

    List<String> commands = new ArrayList<>();
    commands.add(
        ansibleConfig.getAnsibleLocation() + File.separator  + ansibleConfig.getPlaybookCommand());
    commands.add("-i");
    commands.add(
        ansibleConfig.getPlaybookLocation() + File.separator + ansibleConfig.getInventoryName());

    commands.add("-vvvv");
    commands.add("-e");
    commands.add(appservername+"_cluster_name=" + clusterName + " token=" + getToken());

    commands.add(ansibleConfig.getPlaybookLocation() + File.separator+ appservername+"_"
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
      String clusterName,String appservername,String type) throws Exception {
    log.info("executing upgrade_war.yml ");
    SecureRandom random = SecureRandom.getInstanceStrong();
    byte salt[] = new byte[20];
    random.nextBytes(salt);
    List<String> commands = new ArrayList<>();
    commands.add(
        ansibleConfig.getAnsibleLocation() + File.separator + ansibleConfig.getPlaybookCommand());
    commands.add("-i");
    commands.add(
        ansibleConfig.getPlaybookLocation() + File.separator + ansibleConfig.getInventoryName());

    commands.add("-vvvv");
    commands.add("-e");
    commands
        .add("name=" + name + " version=" + version + " groupid=" + groupId +" "+ appservername+ "_cluster_name="
            + clusterName + " token=" + Base64.getEncoder().encodeToString(salt));

    commands.add(ansibleConfig.getPlaybookLocation() + File.separator + appservername+"_"+"upgrade_"+type+"_"
        + ansibleConfig.getUpgradePlaybook());

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


  private String getValueOrDefault(String value) {
    String defaultValue="tcat";
    return Optional.ofNullable(value)
        .filter(s -> s != null && !s.isEmpty()).orElse(defaultValue);
  }
  
  private String getValueOrDefaultType(String value) {
    String defaultValue="war";
    return Optional.ofNullable(value)
        .filter(s -> s != null && !s.isEmpty()).orElse(defaultValue);
  }
}
