package com.example.distcomp.repository

import com.example.distcomp.model.Creator
import org.springframework.stereotype.Repository

@Repository
class CreatorRepository : InMemoryRepository<Creator>() {
    fun findByLogin(login: String) = storage.values.find { it.login == login }
}
