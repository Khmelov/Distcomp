package com.example.distcomp.data.dbo

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import java.time.LocalDateTime

@Entity
@Table(
    name = "tbl_tweet",
    uniqueConstraints = [UniqueConstraint(columnNames = ["creator_id", "title"])]
)
class TweetDbo : BaseDbo() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    var creator: CreatorDbo? = null

    @Column(nullable = false)
    var title: String = ""

    @Column(nullable = false)
    var content: String = ""

    @Column(nullable = false)
    var created: LocalDateTime = LocalDateTime.now()

    @Column(nullable = false)
    var modified: LocalDateTime = LocalDateTime.now()

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "tbl_tweet_sticker",
        joinColumns = [JoinColumn(name = "tweet_id")],
        inverseJoinColumns = [JoinColumn(name = "sticker_id")]
    )
    var stickers: MutableSet<StickerDbo> = mutableSetOf()
}
