package com.example.entitiesapp.repository

import com.example.entitiesapp.model.Comment
import org.springframework.stereotype.Repository

@Repository
class CommentRepository :
    AbstractInMemoryRepository<Comment>()