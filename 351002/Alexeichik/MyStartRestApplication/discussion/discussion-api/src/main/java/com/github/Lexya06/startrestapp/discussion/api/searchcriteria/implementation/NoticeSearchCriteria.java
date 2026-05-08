package com.github.Lexya06.startrestapp.discussion.api.searchcriteria.implementation;

import com.github.Lexya06.startrestapp.discussion.api.searchcriteria.abstraction.BaseSearchCriteria;
import lombok.*;
import lombok.experimental.SuperBuilder;


@Getter
@Setter
@SuperBuilder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class NoticeSearchCriteria extends BaseSearchCriteria {
    private String country;
    private Long articleId;
}
