package com.homel.user_stories.repository;

import com.homel.user_stories.model.Label;
import com.homel.user_stories.model.Notice;
import com.homel.user_stories.model.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoticeRepository extends JpaRepository<Notice, Long> {

}
