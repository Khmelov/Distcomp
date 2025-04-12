package org.example.discussion.entity;
import lombok.Generated;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.mapping.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import lombok.Data;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Table("tbl_message")
@Data
public class Message {

    @PrimaryKeyColumn(name = "country", type = PrimaryKeyType.PARTITIONED, ordinal =0)
    private String country;

    @PrimaryKeyColumn(name = "id", type = PrimaryKeyType.CLUSTERED, ordinal = 1)
    private long id = System.currentTimeMillis() << 10 | (ThreadLocalRandom.current().nextInt(1024) & 0x3FF);

    @PrimaryKeyColumn(name = "storyid",  type = PrimaryKeyType.CLUSTERED, ordinal = 2, ordering = Ordering.ASCENDING)
    private long storyId;

    @Column
    private String content;

}