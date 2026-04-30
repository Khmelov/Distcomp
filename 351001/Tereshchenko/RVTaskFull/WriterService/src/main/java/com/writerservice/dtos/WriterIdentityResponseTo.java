package com.writerservice.dtos;

import com.writerservice.models.WriterRole;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WriterIdentityResponseTo {

    private Long id;

    private String login;

    private String password;

    private String firstname;

    private String lastname;

    private WriterRole role;
}
