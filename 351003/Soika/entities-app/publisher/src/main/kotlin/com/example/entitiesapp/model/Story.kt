package com.example.entitiesapp.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tbl_story", schema = "distcomp")
data class Story(
    override var id: Long? = null,

    @Column(name = "writer_id", nullable = false)
    var writerId: Long,

    @Column(nullable = false, unique = true, length = 64)
    var title: String,

    @Column(nullable = false, length = 2048)
    var content: String,

    @Column(nullable = false)
    var created: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var modified: LocalDateTime = LocalDateTime.now(),

    @ManyToMany(fetch = FetchType.EAGER, cascade = [CascadeType.MERGE, CascadeType.PERSIST])
    @JoinTable(
        name = "tbl_story_mark",
        schema = "distcomp",
        joinColumns = [JoinColumn(name = "story_id")],
        inverseJoinColumns = [JoinColumn(name = "mark_id")]
    )
    var marks: MutableSet<Mark> = mutableSetOf()
) : BaseEntity(id)