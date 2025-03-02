package bsuir.dc.rest.mapper

import bsuir.dc.rest.dto.from.IssueFrom
import bsuir.dc.rest.dto.to.IssueTo
import bsuir.dc.rest.entity.Issue

fun IssueFrom.toEntity(): Issue = Issue(
    id = id,
    writerId = writerId,
    title = title,
    content = content,
)

fun Issue.toResponse(): IssueTo = IssueTo(
    id = id,
    writerId = writerId,
    title = title,
    content = content,
    created = created,
    modified = modified
)