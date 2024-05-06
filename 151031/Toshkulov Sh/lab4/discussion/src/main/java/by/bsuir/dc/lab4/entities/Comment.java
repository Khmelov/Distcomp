package by.bsuir.dc.lab4.entities;


import lombok.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Table("comments")
public class Comment {

    @PrimaryKeyColumn(name="country", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String country = "BY";

    @PrimaryKeyColumn(name="id",ordinal = 0,type = PrimaryKeyType.CLUSTERED)
    private Long id = UUID.randomUUID().getLeastSignificantBits() % 1000000;

    @PrimaryKeyColumn(name="news_id",ordinal = 0,type = PrimaryKeyType.CLUSTERED)
    private Long newsId;

    @Column("content")
    private String content;
}
