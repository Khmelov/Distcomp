package bsuir.dc.publisher.entity

import jakarta.persistence.*

@Entity
@Table(name = "tbl_writer", schema = "public")
data class Writer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, unique = true, length = 64)
    val login: String = "",

    @Column(nullable = false, length = 128)
    val password: String = "",

    @Column(nullable = false, length = 64)
    val firstname: String = "",

    @Column(nullable = false, length = 64)
    val lastname: String = "",

    @OneToMany(mappedBy = "writer", cascade = [CascadeType.ALL], orphanRemoval = true)
    val issues: MutableList<Issue> = mutableListOf()
)
