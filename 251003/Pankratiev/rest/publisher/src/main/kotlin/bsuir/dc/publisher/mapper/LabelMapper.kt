package bsuir.dc.publisher.mapper

import bsuir.dc.publisher.dto.from.LabelRequestTo
import bsuir.dc.publisher.dto.to.LabelResponseTo
import bsuir.dc.publisher.entity.Label

fun LabelRequestTo.toEntity() = Label(
    id = this.id,
    name = this.name
)

fun Label.toResponse() = LabelResponseTo(
    id = this.id,
    name = this.name
)
