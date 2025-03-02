package bsuir.dc.rest.mapper

import bsuir.dc.rest.dto.from.LabelFrom
import bsuir.dc.rest.dto.to.LabelTo
import bsuir.dc.rest.entity.Label

fun LabelFrom.toEntity(): Label = Label(
    id = id,
    name = name
)

fun Label.toResponse(): LabelTo = LabelTo(
    id = id,
    name = name
)