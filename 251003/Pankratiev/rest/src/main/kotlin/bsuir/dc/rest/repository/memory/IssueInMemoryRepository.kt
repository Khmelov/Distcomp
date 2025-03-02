package bsuir.dc.rest.repository.memory

import bsuir.dc.rest.entity.Issue
import org.springframework.stereotype.Repository

@Repository
class IssueInMemoryRepository: InMemoryRepository<Issue>()