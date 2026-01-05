package com.publisher.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_tweet", schema = "distcomp")
public class Tweet {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "tweet_seq")
	@SequenceGenerator(name = "tweet_seq", schema = "distcomp", 
					   sequenceName = "seq_tweet_id", allocationSize = 1)
	private Long id;
	
	@Column(name = "writer_id", nullable = false, insertable = false, updatable = false)
	private Long writerId;
	
	@Column(name = "title", nullable = false, unique = true, length = 64)
	@Size(min = 2, max = 64)
	private String title;
	
	@Column(name = "content", nullable = false, length = 2048)
	@Size(min = 4, max = 2048)
	private String content;
	
	@Column(name = "created", nullable = false)
	private LocalDateTime created = LocalDateTime.now();
	
	@Column(name = "modified", nullable = false)
	private LocalDateTime modified = LocalDateTime.now();
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "writer_id", nullable = false,
				foreignKey = @ForeignKey(name = "fk_tweet_writer"))
	private Writer writer;
	
	@ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "tbl_tweet_label",
        joinColumns = @JoinColumn(name = "tweet_id"),
        inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private List<Label> labels = new ArrayList<>();
	
	public Tweet() {}
	
	public Tweet(Long writerId, String title, String content) {
		this.writerId = writerId;
		this.title = title;
		this.content = content;
		this.created = LocalDateTime.now();
		this.modified = LocalDateTime.now();
	}
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	
	public Long getWriterId() { return writerId; }
	public void setWriterId(Long writerId) { this.writerId = writerId; }
	
	public String getTitle() { return title; }
	public void setTitle(String title) { this.title = title; }
	
	public String getContent() { return content; }
	public void setContent(String content) { this.content = content; }
	
	public LocalDateTime getCreated() { return created; }
	public void setCreated(LocalDateTime created) { this.created = created; }
	
	public LocalDateTime getModified() { return modified; }
	public void setModified(LocalDateTime modified) { this.modified = modified; }
	
	public Writer getWriter() { return writer; }
	public void setWriter(Writer writer) { this.writer = writer; }
	
	public List<Label> getLabels() { return labels; }
	public void setLabels(List<Label> labels) { this.labels = labels; }
}