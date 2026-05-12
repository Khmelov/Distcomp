package com.example.distcomp.data.datasource.creator

import com.example.distcomp.data.datasource.BaseDataSource
import com.example.distcomp.data.dbo.CreatorDbo
import com.example.distcomp.model.Creator

interface CreatorDataSource : BaseDataSource<Creator, CreatorDbo> {
    fun findByLogin(login: String): Creator?
}
