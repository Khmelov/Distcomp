package com.example.distcomp.data.dbo

import jakarta.persistence.Column
import jakarta.persistence.Entity
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
}
