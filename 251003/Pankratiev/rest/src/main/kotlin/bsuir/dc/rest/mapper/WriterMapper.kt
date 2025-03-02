package bsuir.dc.rest.mapper

import bsuir.dc.rest.dto.from.WriterFrom
import bsuir.dc.rest.dto.to.WriterTo
import bsuir.dc.rest.entity.Writer

fun WriterFrom.toEntity(): Writer = Writer(
    id = id,
    login = this.login,
    password = this.password,
    firstname = this.firstname,
    lastname = this.lastname
)

fun Writer.toResponse(): WriterTo = WriterTo(
    id = this.id,
    login = this.login,
    firstname = this.firstname,
    lastname = this.lastname
)
