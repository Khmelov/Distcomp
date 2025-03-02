package bsuir.dc.rest.entity

data class IssueLabel(
    override var id: Long = 0L,
    val issueId: Long,
    val labelId: Long
) : Identifiable
