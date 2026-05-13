package com.example.distcomp.repository

import com.example.distcomp.model.Creator

interface CreatorRepository : CrudRepository<Creator> {
    fun findByLogin(login: String): Creator?
}
