package by.bsuir.publisher.dto.responses;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@NoArgsConstructor
@Data
public abstract class BaseResponseDto {
    private Long id;
}
