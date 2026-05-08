package com.example.entitiesapp.model

import jakarta.persistence.*

@Entity
@Table(name = "tbl_mark", schema = "distcomp")
data class Mark(
    override var id: Long? = null,

    @Column(nullable = false, unique = true, length = 32)
    var name: String
) : BaseEntity(id)