package bsuir.dc.rest.mapper

import bsuir.dc.rest.dto.from.IssueLabelFrom
import bsuir.dc.rest.dto.to.IssueLabelTo
import bsuir.dc.rest.entity.IssueLabel

fun IssueLabelFrom.toEntity(): IssueLabel = IssueLabel(
    issueId = issueId,
    labelId = labelId,
)

fun IssueLabel.toResponse(): IssueLabelTo = IssueLabelTo(
    issueId = issueId,
    labelId = labelId,
)
