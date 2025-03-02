package bsuir.dc.rest.entity

data class Label(
    override var id: Long = 0L,
    val name: String
) : Identifiable
