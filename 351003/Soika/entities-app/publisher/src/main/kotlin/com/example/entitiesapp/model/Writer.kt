package com.example.entitiesapp.model

import jakarta.persistence.*
import com.example.entitiesapp.dto.Role

@Entity
@Table(name = "tbl_writer", schema = "distcomp")
data class Writer(
    override var id: Long? = null,

    @Column(nullable = false, unique = true, length = 64)
    var login: String,

    @Column(nullable = false, length = 128)
    var password: String,

    @Column(nullable = false, length = 64)
    var firstname: String,

    @Column(nullable = false, length = 64)
    var lastname: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    var role: Role = Role.CUSTOMER
) : BaseEntity(id)