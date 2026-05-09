package com.distcomp.model.note;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;

@Table("tbl_note")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Note {

    @PrimaryKey
    private NoteKey key;

    private String content;

    public Long getTopicId() {
        return key != null ? key.getTopicId() : null;
    }

    public Long getId() {
        return key != null ? key.getId() : null;
    }

    public String getCountry() {
        return key != null ? key.getCountry() : null;
    }

    @PrimaryKeyClass
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NoteKey implements Serializable {
        @PrimaryKeyColumn(name = "country", type = PrimaryKeyType.PARTITIONED)
        private String country;

        @PrimaryKeyColumn(name = "topic_id", type = PrimaryKeyType.CLUSTERED)
        private Long topicId;

        @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.CLUSTERED)
        private Long id;
    }
}