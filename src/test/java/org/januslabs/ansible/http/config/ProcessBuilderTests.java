package org.januslabs.ansible.http.config;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.lang.ProcessBuilder.Redirect;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.Assert;
import org.xnio.streams.ReaderInputStream;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.ProcessResult;

public class ProcessBuilderTests {

  @Ignore
  public void login() throws Exception {
    List<String> commands = new ArrayList<>();
    commands.add("ssh");
    // commands.add("-t");
    // commands.add ("-o StrictHostKeyChecking=no");
    commands.add("ns48235@msp0lnans001.etdbw.com");
    ProcessBuilder processBuilder = new ProcessBuilder();
    processBuilder.command(commands);
    processBuilder.redirectErrorStream(true);
    Process process = processBuilder.start();
    processBuilder.redirectOutput(Redirect.PIPE);
    processBuilder.redirectInput(Redirect.PIPE);
    processBuilder.redirectError(Redirect.PIPE);

    String passwd = "fortis90";
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
    writer.write(passwd, 0, passwd.length());
    writer.newLine();
    writer.close();


    Integer exitStatus = process.waitFor();
    System.out.println(exitStatus);
    process.destroy();
  }

  @Ignore
  public void login2() throws Exception {
    List<String> commands = new ArrayList<>();
    commands.add("ssh");
    // commands.add("-t");
    // commands.add ("-o StrictHostKeyChecking=no");
    commands.add("ansible@msp0lnans001.etdbw.com");
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ProcessResult result1 = new ProcessExecutor(commands).readOutput(true).redirectOutput(out)
        .redirectInput(new ReaderInputStream(new StringReader("@n$ible.\\r\\n"))).execute();

    System.out.println(result1.outputString());
    // out.write("@n$ible.".getBytes());
    result1.getOutput().getLines().forEach(i -> System.out.println(i));
    // ProcessResult result2=new ProcessExecutor("ls -lrt").readOutput(true).execute();
  }

  @Test
  public void generateRandomString() throws Exception {
    SecureRandom random = SecureRandom.getInstanceStrong();
    byte salt[] = new byte[50];
    random.nextBytes(salt);
    String encodedString = Base64.getEncoder().encodeToString(salt);
    System.out.println(encodedString);
    Assert.notNull(encodedString);
  }
  
  @Test
  public void replaceStringTest()
  {
    String environment="dev";
    String testString="hosts.env";
    String inventoryFileName = testString.replaceAll("env", environment);
    System.out.println(inventoryFileName);
    String invFileName=inventoryFileName.substring(0,inventoryFileName.indexOf("."));
    System.out.println(invFileName);
   
    
  }
}
