package com.distcomp.model.m2m;

import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("tbl_topic_tag")
@IdClass(TopicTagId.class)
public class TopicTag {
    @Id
    private Long topicId;
    @Id
    private Long tagId;
}
