package com.distcomp.entity

import jakarta.persistence.*

@Entity
@Table(name = "tbl_marker")
class Marker(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long = 0,
    var name: String,
)