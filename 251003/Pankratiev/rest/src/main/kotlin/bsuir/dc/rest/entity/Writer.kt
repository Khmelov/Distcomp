package bsuir.dc.rest.entity

data class Writer(
    override var id: Long = 0L,
    val login: String,
    val password: String,
    val firstname: String,
    val lastname: String
) : Identifiable
