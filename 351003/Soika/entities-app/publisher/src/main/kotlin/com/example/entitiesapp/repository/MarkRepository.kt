package com.example.entitiesapp.repository

import com.example.entitiesapp.model.Mark
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MarkRepository : JpaRepository<Mark, Long> {
    fun findByName(name: String): Mark?
}