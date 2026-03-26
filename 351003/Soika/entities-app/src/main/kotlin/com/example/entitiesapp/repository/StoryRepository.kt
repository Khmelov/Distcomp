package com.example.entitiesapp.repository

import com.example.entitiesapp.model.Story
import org.springframework.stereotype.Repository

@Repository
class StoryRepository :
    AbstractInMemoryRepository<Story>()