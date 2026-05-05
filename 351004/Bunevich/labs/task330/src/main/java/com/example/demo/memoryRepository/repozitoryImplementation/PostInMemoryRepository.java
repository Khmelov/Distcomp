package com.example.demo.memoryRepository.repozitoryImplementation;

import com.example.demo.memoryRepository.InMemoryRealization;
import com.example.demo.models.Post;
import org.springframework.stereotype.Repository;

@Repository
public class PostInMemoryRepository extends InMemoryRealization<Post> {
}
