package com.publisher.dto.response;

import com.publisher.entity.Label;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class LabelResponseTo {
	
	private Long id;
	private String name;
	
	public LabelResponseTo() {}
	
	public LabelResponseTo(Label label) {
		this.id = label.getId();
		this.name = label.getName();
	}
	
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	
	public String getName() { return name; }
	public void setName(String name) { this.name = name; }
}