package bsuir.dc.publisher.dto.from

import jakarta.validation.constraints.Size

data class WriterRequestTo(
    val id: Long = 0,

    @field:Size(min = 2, max = 64)
    val login: String = "",

    @field:Size(min = 8, max = 128)
    val password: String = "",

    @field:Size(min = 2, max = 64)
    val firstname: String = "",

    @field:Size(min = 2, max = 64)
    val lastname: String = ""
)
