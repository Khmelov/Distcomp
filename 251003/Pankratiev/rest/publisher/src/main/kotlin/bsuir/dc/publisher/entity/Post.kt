package bsuir.dc.publisher.entity

import jakarta.persistence.*

@Entity
@Table(name = "tbl_post", schema = "public")
data class Post(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issueId", nullable = false)
    val issue: Issue = Issue(),

    @Column(nullable = false, length = 2048)
    val content: String  = ""
)
