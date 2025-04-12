package bsuir.dc.publisher.mapper

import bsuir.dc.publisher.dto.from.IssueRequestTo
import bsuir.dc.publisher.dto.to.IssueResponseTo
import bsuir.dc.publisher.entity.Issue
import bsuir.dc.publisher.entity.Label
import bsuir.dc.publisher.entity.Writer

fun IssueRequestTo.toEntity(writer: Writer, labels: MutableSet<Label>) = Issue(
    id = this.id,
    writer = writer,
    title = this.title,
    content = this.content,
    labels = labels,
)

fun Issue.toResponse() = IssueResponseTo(
    id = this.id,
    writerId = this.writer.id,
    title = this.title,
    content = this.content,
    created = this.created,
    modified = this.modified
)
