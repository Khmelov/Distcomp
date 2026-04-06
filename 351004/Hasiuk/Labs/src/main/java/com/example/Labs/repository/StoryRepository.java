package com.example.Labs.repository;

import com.example.Labs.entity.Mark;
import com.example.Labs.entity.Story;
import org.springframework.stereotype.Repository;

@Repository
public interface StoryRepository extends BaseRepository<Story, Long> {
    long countByMarksContains(Mark mark);
}