package com.distcomp.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

@Entity
@Table(name = "tbl_news")
class News(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long? = null,
    var title: String,
    var content: String,
    var created: LocalDateTime = LocalDateTime.now(),
    var modified: LocalDateTime = LocalDateTime.now(),
    @ManyToOne
    @JoinColumn(name = "user_id")
    var user: User? = null
)