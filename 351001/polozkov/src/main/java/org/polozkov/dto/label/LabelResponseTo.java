package org.polozkov.dto.label;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LabelResponseTo {

    private Long id;

    @JsonProperty("name")
    private String name;
}
