package by.bsuir.distcomp.discussion.domain;

import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.Objects;

/**
 * Партиционирование по tweet_id: реакции одного твита в одной партиции (эффективные запросы по твиту).
 * Кластерный ключ id — уникальность реакции внутри партиции.
 */
@PrimaryKeyClass
public class ReactionKey implements Serializable {

    @PrimaryKeyColumn(name = "tweet_id", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private Long tweetId;

    @PrimaryKeyColumn(name = "id", ordinal = 1, type = PrimaryKeyType.CLUSTERED)
    private Long id;

    public Long getTweetId() {
        return tweetId;
    }

    public void setTweetId(Long tweetId) {
        this.tweetId = tweetId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReactionKey that = (ReactionKey) o;
        return Objects.equals(tweetId, that.tweetId) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tweetId, id);
    }
}
