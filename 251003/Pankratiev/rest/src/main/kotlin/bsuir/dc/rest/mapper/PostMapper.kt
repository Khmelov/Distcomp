package bsuir.dc.rest.mapper

import bsuir.dc.rest.dto.from.PostFrom
import bsuir.dc.rest.dto.to.PostTo
import bsuir.dc.rest.entity.Post

fun PostFrom.toEntity(): Post = Post(
    id = id,
    issueId = issueId,
    content = content
)

fun Post.toResponse(): PostTo = PostTo(
    id = id,
    issueId = issueId,
    content = content
)
