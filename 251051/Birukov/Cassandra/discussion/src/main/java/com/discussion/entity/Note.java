package com.discussion.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.cassandra.core.mapping.*;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "tbl_note")
public class Note {
	
	@PrimaryKey
    private NoteKey key;
    
    @Column("content")
    private String content;
	
	public Note() {}
	
	public Note(NoteKey key, String content) {
		this.key = key;
		this.content = content;
	}
	
	public NoteKey getKey() { return key; }
    public void setKey(NoteKey key) { this.key = key; }
	
	public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @PrimaryKeyClass
    public static class NoteKey {
        
        @PrimaryKeyColumn(
            name = "country",
            type = PrimaryKeyType.PARTITIONED,
            ordinal = 0
        )
        private String country;
		
		@PrimaryKeyColumn(
            name = "tweet_id",
            type = PrimaryKeyType.CLUSTERED,
            ordinal = 1
        )
        private Long tweetId;
        
        @PrimaryKeyColumn(
            name = "id",
            type = PrimaryKeyType.CLUSTERED,
            ordinal = 1
        )
        private Long id;
		
		public NoteKey() {}
        
        public NoteKey(String country, Long tweetId, Long id) {
            this.country = country;
			this.tweetId = tweetId;
            this.id = id;
        }
		
		public String getCountry() { return country; }
        public void setCountry(String country) { this.country = country; }
		
		public Long getTweetId() { return tweetId; }
        public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
		
		public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }
}