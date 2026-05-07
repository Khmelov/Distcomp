package com.github.Lexya06.startrestapp.discussion.impl.model.entity.realization;

import com.github.Lexya06.startrestapp.discussion.api.dto.notice.NoticeState;
import com.github.Lexya06.startrestapp.discussion.impl.model.entity.abstraction.BaseEntity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.Table;

@Getter
@Setter
@Table("tbl_notice")
public class Notice extends BaseEntity<NoticeKey> {
    private String content;
    private NoticeState state;

}
