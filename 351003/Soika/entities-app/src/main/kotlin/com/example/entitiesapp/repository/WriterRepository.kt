package com.example.entitiesapp.repository

import com.example.entitiesapp.model.Writer
import org.springframework.stereotype.Repository

@Repository
class WriterRepository :
    AbstractInMemoryRepository<Writer>()