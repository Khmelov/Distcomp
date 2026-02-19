package com.distcomp.entity

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Entity
@Table(name = "tbl_marker")
class Marker(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Long,
    var name: String,
)