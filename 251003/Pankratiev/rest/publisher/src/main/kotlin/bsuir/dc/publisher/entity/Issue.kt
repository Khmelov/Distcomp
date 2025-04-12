package bsuir.dc.publisher.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "tbl_issue", schema = "public")
data class Issue(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writerId", nullable = false)
    val writer: Writer = Writer(),

    @Column(nullable = false, unique = true, length = 64)
    val title: String = "",

    @Column(nullable = false, length = 2048)
    val content: String = "",

    @Column(nullable = false)
    val created: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var modified: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "issue", cascade = [CascadeType.ALL], orphanRemoval = true)
    val posts: MutableList<Post> = mutableListOf(),

    @ManyToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinTable(
        name = "tbl_issue_label",
        schema = "public",
        joinColumns = [JoinColumn(name = "issueId")],
        inverseJoinColumns = [JoinColumn(name = "labelId")]
    )
    val labels: MutableSet<Label> = mutableSetOf()
)
