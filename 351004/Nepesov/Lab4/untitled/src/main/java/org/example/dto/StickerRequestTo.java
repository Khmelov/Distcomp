package org.example.dto;

import com.fasterxml.jackson.annotation.JsonRootName;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StickerRequestTo {
    private Long id;

    @Size(min = 2, max = 32)
    private String name;
}