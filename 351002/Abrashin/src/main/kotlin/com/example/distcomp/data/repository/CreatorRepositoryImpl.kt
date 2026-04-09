package com.example.distcomp.data.repository

import com.example.distcomp.data.datasource.creator.CreatorDataSource
import com.example.distcomp.data.dbo.CreatorDbo
import com.example.distcomp.model.Creator
import org.springframework.stereotype.Repository

import com.example.distcomp.repository.CreatorRepository

@Repository
class CreatorRepositoryImpl(
    private val dataSource: CreatorDataSource
) : DataSourceRepository<Creator, CreatorDbo>(dataSource), CreatorRepository {
    override fun findByLogin(login: String): Creator? = dataSource.findByLogin(login)
}
