package com.example.demo.memoryRepository.repozitoryImplementation;

import com.example.demo.memoryRepository.InMemoryRealization;
import com.example.demo.models.Tag;
import org.springframework.stereotype.Repository;

@Repository
public class TagInMemoryRepository extends InMemoryRealization<Tag> {
}
