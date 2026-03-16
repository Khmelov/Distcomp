package com.distcomp.entity

import jakarta.persistence.*

@Entity
@Table(name = "tbl_notice")
class Notice(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long,
    var content: String,
    @ManyToOne
    @JoinColumn(name = "news_id")
    var news: News? = null
)