package org.januslabs.ansible.http.endpoints;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@ConfigurationProperties("ansible")
@Component
public class AnsibleConfiguration {

  @Value("${ansible.basedir.location}")
  private String ansibleLocation;
  @Value("${ansible.playbookdir.location}")
  private String playbookLocation;
  @Value("${ansible.ping.playbook.name}")
  private String pingPlaybook;
  @Value("${ansible.playbook.cmd}")
  private String playbookCommand;
  @Value("${ansible.inventory.name}")
  private String inventoryName;
  @Value("${ansible.adhoc.cmd}")
  private String adhocName;
  @Value("${ansible.upgrade.playbook.name}")
  private String upgradePlaybook;
  @Value("${ansible.restart.playbook.name}")
  private String bounceServerPlaybook;
  @Value("${ansible.status.playbook.name}")
  private String statusServerPlaybook;
}
