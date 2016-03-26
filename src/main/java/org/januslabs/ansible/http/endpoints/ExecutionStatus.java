package org.januslabs.ansible.http.endpoints;

import lombok.Data;

@Data
public class ExecutionStatus {

  private Integer code;
  private String output;

}
