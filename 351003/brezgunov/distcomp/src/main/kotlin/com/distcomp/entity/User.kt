package com.distcomp.entity

import jakarta.persistence.*

@Entity
@Table(name = "tbl_user")
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    var login: String,
    var password: String,
    var firstname: String,
    var lastname: String,
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    var news: MutableList<News>? = null,

    @Enumerated(EnumType.STRING)
    var role: Role = Role.CUSTOMER
)

enum class Role {
    ADMIN, CUSTOMER
}