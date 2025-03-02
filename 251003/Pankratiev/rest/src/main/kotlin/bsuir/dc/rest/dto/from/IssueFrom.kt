package bsuir.dc.rest.dto.from

import jakarta.validation.constraints.Size

data class IssueFrom(
    val id: Long = 0,
    val writerId: Long = 0,

    @field:Size(min = 2, max = 64)
    val title: String = "",

    @field:Size(min = 4, max = 2048)
    val content: String = ""
)
