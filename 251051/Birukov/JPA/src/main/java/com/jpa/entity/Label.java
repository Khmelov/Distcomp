package com.jpa.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_label", schema = "distcomp")
public class Label {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "label_seq")
	@SequenceGenerator(name = "label_seq", schema = "distcomp", 
					   sequenceName = "seq_label_id", allocationSize = 1)
	private Long id;
	
	@Column(name = "name", nullable = false, unique = true, length = 32)
	@Size(min = 2, max = 32)
	private String name;
	
	@ManyToMany(
        mappedBy = "labels",
        fetch = FetchType.LAZY
    )
    private List<Tweet> tweets = new ArrayList<>();
	
	public Label() {}
	
	public Label(String name) { this.name = name; }
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
	
	public List<Tweet> getTweets() { return tweets; }
	public void setTweets(List<Tweet> tweets) { this.tweets = tweets; }
}