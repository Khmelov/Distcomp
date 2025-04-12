package by.yelkin.TopicService.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Topic extends BaseEntity {
    private Creator creator;
    private String title;
    private String content;
    private LocalDateTime created;
    private LocalDateTime modified;
}
