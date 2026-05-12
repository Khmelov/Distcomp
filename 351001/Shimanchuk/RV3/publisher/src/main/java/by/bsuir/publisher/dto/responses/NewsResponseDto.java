package by.bsuir.publisher.dto.responses;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@SuperBuilder
@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class NewsResponseDto extends BaseResponseDto {
    private Long writerId;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
}
