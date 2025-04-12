package bsuir.dc.publisher.mapper

import bsuir.dc.publisher.dto.from.PostRequestTo
import bsuir.dc.publisher.dto.to.PostResponseTo
import bsuir.dc.publisher.entity.Issue
import bsuir.dc.publisher.entity.Post

fun PostRequestTo.toEntity(issue: Issue): Post {
    return Post(
        id = this.id,
        issue = issue,
        content = this.content,
    )
}

fun Post.toResponse(): PostResponseTo {
    return PostResponseTo(
        id = this.id,
        issueId = this.issue.id,
        content = this.content
    )
}
