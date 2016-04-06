package org.januslabs.ansible.http.endpoints;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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
  public Response executePingPlaybook() throws Exception {
    log.info("executing hello.yml ");
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
    String key = "STATUS";
    String groupId, version, name, clusterName,initdServiceName;
    MultivaluedMap<String, String> queryParams = uriInfo.getQueryParameters();
    key = queryParams.getFirst("key");
    groupId = queryParams.getFirst("groupId");
    version = queryParams.getFirst("version");
    name = queryParams.getFirst("name");
    clusterName = queryParams.getFirst("clusterName");
    initdServiceName=queryParams.getFirst("initdServiceName");
    log.info("invoking adhoc command for key {} ", key);
    List<String> commands = new ArrayList<String>();
    commands
        .add(ansibleConfig.getAnsibleLocation() + File.separator + ansibleConfig.getAdhocName());
    commands.add("-i");
    commands.add(
        ansibleConfig.getPlaybookLocation() + File.separator + ansibleConfig.getInventoryName());
    commands.add("aebbackend");
    commands.add("-a");
    switch (key.toUpperCase()) {
      case "STATUS":
        commands.add("/etc/init.d/"+initdServiceName+" status");
        commands.add("-m");
        commands.add("command");
        break;
      case "DEPLOY":
        return executeDeployPlaybook(groupId, name, version, clusterName);
      case "BOUNCE":
        commands.add("name="+initdServiceName+"=restarted");
        commands.add("-m");
        commands.add("service");
        break;
      default:
        break;
    }

    commands.add("-vvvv");
    commands.add("--become");
    log.info("executing adhoc commands {} ", commands);
    String processOutput = new ProcessExecutor(commands).readOutput(true).destroyOnExit().execute()
        .getOutput().getUTF8();

    ExecutionStatus status = new ExecutionStatus();
    if (processOutput != null && processOutput.contains("ERROR"))
      status.setCode(-1);
    else
      status.setCode(0);
    status.setOutput(processOutput);
    log.trace("execution result {}  ", processOutput);
    log.debug("execution status {}  ", status);

    return Response.ok().entity(status).status(Status.OK).type(MediaType.APPLICATION_JSON).build();
  }

  private Response executeDeployPlaybook(String groupId, String name, String version,
      String clusterName) throws Exception {
    log.info("executing upgrade_war.yml ");
    List<String> commands = new ArrayList<String>();
    commands.add(
        ansibleConfig.getAnsibleLocation() + File.separator + ansibleConfig.getPlaybookCommand());
    commands.add("-i");
    commands.add(
        ansibleConfig.getPlaybookLocation() + File.separator + ansibleConfig.getInventoryName());
    
    commands.add("-vvvv");
    commands.add("-e");
    commands.add("name=" + name +" version=" + version + " groupid=" + groupId+ " tcat_cluster_name=" + clusterName);
  
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

  @Path("/check/{value}")
  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public Response passcheck(@javax.ws.rs.PathParam("value")String value) throws Exception {
    return Response.ok().entity("@n$ible.").status(Status.OK).type(MediaType.TEXT_PLAIN)
        .build();

  }


}
