package bsuir.dc.rest.repository.memory

import bsuir.dc.rest.entity.IssueLabel
import org.springframework.stereotype.Repository

@Repository
class IssueLabelInMemoryRepository: InMemoryRepository<IssueLabel>()