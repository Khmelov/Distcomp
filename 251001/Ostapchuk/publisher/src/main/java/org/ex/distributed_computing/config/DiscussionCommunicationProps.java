package org.ex.distributed_computing.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "discussion")
public class DiscussionCommunicationProps {

  private String address;
  private String baseApiPath;
}
