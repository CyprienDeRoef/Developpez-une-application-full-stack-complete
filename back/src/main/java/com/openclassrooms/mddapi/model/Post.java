package com.openclassrooms.mddapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "posts")
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="post_id")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name = "topic_id")
	private Topic topic;
	
	// TODO : to finish...

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}
		
	
}
