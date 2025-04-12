package bsuir.dc.discussion.mapper

import bsuir.dc.discussion.dto.from.PostRequestTo
import bsuir.dc.discussion.dto.to.PostResponseTo
import bsuir.dc.discussion.entity.Post
import bsuir.dc.discussion.entity.PostKey

fun PostRequestTo.toEntity(country: String): Post {
    return Post(
        PostKey(country = country, id = this.id),
        issueId = this.issueId,
        content = this.content,
    )
}

fun Post.toResponse(): PostResponseTo {
    return PostResponseTo(
        id = this.key.id,
        issueId = this.issueId,
        content = this.content
    )
}

