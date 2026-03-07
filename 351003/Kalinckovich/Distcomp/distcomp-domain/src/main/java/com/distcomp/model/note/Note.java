package com.distcomp.model.note;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.relational.core.mapping.Table;
import jakarta.validation.constraints.Size;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.UUID;

@Table("tbl_note")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Note {

    @PrimaryKey
    private NoteKey key;

    @Size(min = 2, max = 2048)
    private String content;


    public Long getTopicId() {
        return key != null ? key.getTopicId() : null;
    }

    public UUID getId() {
        return key != null ? key.getId() : null;
    }

    @PrimaryKeyClass
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoteKey implements Serializable {
        @PrimaryKeyColumn(name = "topic_id", type = PrimaryKeyType.PARTITIONED)
        private Long topicId;

        @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.CLUSTERED)
        private UUID id;
    }
}