package com.sergey.orsik.dto.response;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonRootName("label")
public class LabelResponseTo {

    private Long id;
    private String name;
}
