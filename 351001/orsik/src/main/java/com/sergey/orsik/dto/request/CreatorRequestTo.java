package com.sergey.orsik.dto.request;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonRootName("creator")
public class CreatorRequestTo {

    private Long id;
    private String login;
    private String firstname;
    private String lastname;
}
