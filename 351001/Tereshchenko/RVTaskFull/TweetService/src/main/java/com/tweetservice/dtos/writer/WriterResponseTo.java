package com.tweetservice.dtos.writer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WriterResponseTo {

    public Long id;

    public String login;

    public String firstname;

    public String lastname;
}
