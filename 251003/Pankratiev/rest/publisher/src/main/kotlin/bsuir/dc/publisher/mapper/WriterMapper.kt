package bsuir.dc.publisher.mapper

import bsuir.dc.publisher.dto.from.WriterRequestTo
import bsuir.dc.publisher.dto.to.WriterResponseTo
import bsuir.dc.publisher.entity.Writer

fun WriterRequestTo.toEntity(): Writer {
    return Writer(
        id = this.id,
        login = this.login,
        password = this.password,
        firstname = this.firstname,
        lastname = this.lastname
    )
}

fun Writer.toResponse(): WriterResponseTo {
    return WriterResponseTo(
        id = this.id,
        login = this.login,
        firstname = this.firstname,
        lastname = this.lastname
    )
}
