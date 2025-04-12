package bsuir.dc.publisher.dto.from

import jakarta.validation.constraints.Size

data class IssueRequestTo(
    val id: Long = 0,
    val writerId: Long = 0,
    val labels: List<String> = listOf(),

    @field:Size(min = 2, max = 64)
    val title: String = "",

    @field:Size(min = 4, max = 2048)
    val content: String = ""
)
