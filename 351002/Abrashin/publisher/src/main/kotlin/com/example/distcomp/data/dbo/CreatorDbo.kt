package com.example.distcomp.data.dbo

import com.example.distcomp.model.CreatorRole
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "tbl_creator")
class CreatorDbo : BaseDbo() {
    @Column(unique = true, nullable = false)
    var login: String = ""

    @Column(nullable = false)
    var password: String = ""

    @Column(nullable = false)
    var firstname: String = ""

    @Column(nullable = false)
    var lastname: String = ""

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var role: CreatorRole = CreatorRole.CUSTOMER
}
