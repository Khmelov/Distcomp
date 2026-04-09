package com.example.distcomp.data.dbo

import jakarta.persistence.*
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction

@Entity
@Table(name = "tbl_note")
class NoteDbo : BaseDbo() {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tweet_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    var tweet: TweetDbo? = null

    @Column(nullable = false)
    var content: String = ""
}
