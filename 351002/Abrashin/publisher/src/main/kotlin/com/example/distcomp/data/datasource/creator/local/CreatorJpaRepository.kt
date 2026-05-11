package com.example.distcomp.data.datasource.creator.local

import com.example.distcomp.data.dbo.CreatorDbo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import java.util.*

interface CreatorJpaRepository : JpaRepository<CreatorDbo, Long>, JpaSpecificationExecutor<CreatorDbo> {
    fun findByLogin(login: String): Optional<CreatorDbo>
}
