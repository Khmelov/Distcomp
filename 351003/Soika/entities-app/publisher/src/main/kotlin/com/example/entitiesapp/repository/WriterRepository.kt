package com.example.entitiesapp.repository

import com.example.entitiesapp.model.Writer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WriterRepository : JpaRepository<Writer, Long> {
    fun findByLogin(login: String): Writer?
}