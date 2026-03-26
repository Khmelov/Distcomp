package com.distcomp.model.m2m;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopicTagId implements Serializable {
    private Long topicId;
    private Long tagId;
}
