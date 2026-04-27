package com.example.demo.memoryRepository.repozitoryImplementation;

import com.example.demo.memoryRepository.InMemoryRealization;
import com.example.demo.models.Story;
import org.springframework.stereotype.Repository;

@Repository
public class StoryInMemoryRepository extends InMemoryRealization<Story> {
}
