package com.publisher.dto.response;

import com.publisher.entity.Writer;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class WriterResponseTo {
	
	private Long id;
	private String login;
	private String password;
	private String firstname;
	private String lastname;
	
	public WriterResponseTo() {}
	
	public WriterResponseTo(Writer writer) {
		this.id = writer.getId();
		this.login = writer.getLogin();
		this.password = writer.getPassword();
		this.firstname = writer.getFirstname();
		this.lastname = writer.getLastname();
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
}