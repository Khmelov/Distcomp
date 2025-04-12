package bsuir.dc.publisher.entity

import jakarta.persistence.*

@Entity
@Table(name = "tbl_label", schema = "public")
data class Label(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, unique = true, length = 32)
    val name: String  = "",
)
