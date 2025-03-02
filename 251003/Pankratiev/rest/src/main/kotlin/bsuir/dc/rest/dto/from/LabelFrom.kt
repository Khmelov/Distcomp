package bsuir.dc.rest.dto.from

import jakarta.validation.constraints.Size

data class LabelFrom(
    val id: Long = 0,

    @field:Size(min = 2, max = 32)
    val name: String = ""
)
