package com.example.entitiesapp.repository

import com.example.entitiesapp.model.Mark
import org.springframework.stereotype.Repository

@Repository
class MarkRepository :
    AbstractInMemoryRepository<Mark>()