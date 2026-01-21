package com.example.app.model;

import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;

@Table("tbl_reaction") // Для Cassandra используем @Table вместо @Entity
public class Reaction {
    
    @PrimaryKey
    private ReactionKey key;
    
    @NotBlank 
    @Size(min = 2, max = 2048)
    private String content;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Конструкторы
    public Reaction() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Reaction(ReactionKey key, String content) {
        this.key = key;
        this.content = content;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Геттеры и сеттеры
    public ReactionKey getKey() {
        return key;
    }
    
    public void setKey(ReactionKey key) {
        this.key = key;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Вспомогательные методы для удобства
    public Long getId() {
        return key != null ? key.getId() : null;
    }
    
    public void setId(Long id) {
        if (key == null) {
            key = new ReactionKey();
        }
        key.setId(id);
    }
    
    public Long getTweetId() {
        return key != null ? key.getTweetId() : null;
    }
    
    public void setTweetId(Long tweetId) {
        if (key == null) {
            key = new ReactionKey();
        }
        key.setTweetId(tweetId);
    }
    
    public String getCountry() {
        return key != null ? key.getCountry() : null;
    }
    
    public void setCountry(String country) {
        if (key == null) {
            key = new ReactionKey();
        }
        key.setCountry(country);
    }
}

@PrimaryKeyClass
public class ReactionKey implements Serializable {
    
    @PrimaryKeyColumn(name = "country", type = PrimaryKeyType.PARTITIONED)
    private String country = "global"; // Значение по умолчанию
    
    @PrimaryKeyColumn(name = "tweet_id", type = PrimaryKeyType.PARTITIONED)
    private Long tweetId;
    
    @PrimaryKeyColumn(name = "id", ordinal = 0)
    private Long id;
    
    // Конструкторы
    public ReactionKey() {}
    
    public ReactionKey(String country, Long tweetId, Long id) {
        this.country = country;
        this.tweetId = tweetId;
        this.id = id;
    }
    
    // Геттеры и сеттеры
    public String getCountry() {
        return country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        ReactionKey that = (ReactionKey) o;
        
        if (!country.equals(that.country)) return false;
        if (!tweetId.equals(that.tweetId)) return false;
        return id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        int result = country.hashCode();
        result = 31 * result + tweetId.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
    
    @Override
    public String toString() {
        return "ReactionKey{" +
                "country='" + country + '\'' +
                ", tweetId=" + tweetId +
                ", id=" + id +
                '}';
    }
}