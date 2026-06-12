package by.bsuir.discussion.dto.requests;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public abstract class BaseRequestDto {
    private Long id;
}
