package com.homel.user_stories.repository;

import com.homel.user_stories.model.Label;
import com.homel.user_stories.model.Story;
import com.homel.user_stories.model.User;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StoryRepository extends JpaRepository<Story, Long> {

}
