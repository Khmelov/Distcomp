package org.discussion.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

@Table("tbl_notice")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notice {

    @PrimaryKey
    private NoticeKey key;
    private String content;
}

