package com.github.Lexya06.startrestapp.discussion.api.dto;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class PagedResponse<T> {
    List<T> data;
    String nextPagingState;

}
