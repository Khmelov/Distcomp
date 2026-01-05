package com.publisher.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_writer", schema = "distcomp")
public class Writer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "writer_seq")
	@SequenceGenerator(name = "writer_seq", schema = "distcomp", 
					   sequenceName = "seq_writer_id", allocationSize = 1)
	private Long id;
	
	@Column(name = "login", nullable = false, unique = true, length = 64)
	@Size(min = 2, max = 64)
	private String login;
	
	@Column(name = "password", nullable = false, length = 128)
	@Size(min = 8, max = 128)
	private String password;
	
	@Column(name = "firstname", nullable = false, length = 64)
	@Size(min = 2, max = 64)
	private String firstname;
	
	@Column(name = "lastname", nullable = false, length = 64)
	@Size(min = 2, max = 64)
	private String lastname;
	
	@OneToMany(mappedBy = "writer", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<Tweet> tweets = new ArrayList<>();
	
	public Writer() {}
	
	public Writer(String login, String password, String firstname, String lastname) {
		this.login = login;
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
	}
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	
	public String getLogin() { return login; }
	public void setLogin(String login) { this.login = login; }
	
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	
	public String getFirstname() { return firstname; }
	public void setFirstname(String firstname) { this.firstname = firstname; }
	
	public String getLastname() { return lastname; }
	public void setLastname(String lastname) { this.lastname = lastname; }
	
	public List<Tweet> getTweets() { return tweets; }
	public void setTweets(List<Tweet> tweets) { this.tweets = tweets; }
}