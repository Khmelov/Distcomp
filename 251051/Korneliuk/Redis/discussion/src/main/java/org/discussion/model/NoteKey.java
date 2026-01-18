package org.discussion.model;

import lombok.Value;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;


@PrimaryKeyClass
@Value
public class NoteKey {
    @PrimaryKeyColumn(type = PrimaryKeyType.PARTITIONED)
    String country;

    @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED)
    Long issueId;

    @PrimaryKeyColumn(type = PrimaryKeyType.CLUSTERED)
    Long id;
}
