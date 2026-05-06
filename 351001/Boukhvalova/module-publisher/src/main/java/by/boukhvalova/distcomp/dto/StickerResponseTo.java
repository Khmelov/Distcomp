package by.boukhvalova.distcomp.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class StickerResponseTo {
    long id;
    @Size(min = 2, max = 32)
    String name;
}
