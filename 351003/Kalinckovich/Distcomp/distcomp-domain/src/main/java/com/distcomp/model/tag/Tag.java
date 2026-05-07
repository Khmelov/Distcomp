package com.distcomp.model.tag;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.validation.constraints.Size;
import lombok.*;

@Table(name = "tbl_tag", schema = "distcomp")
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Tag {
    @Id
    private Long id;

    @Size(min = 2, max = 32, message = "Tag name size must be in range from 2 to 32")
    private String name;
}
