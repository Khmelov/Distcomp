package bsuir.dc.rest.repository.memory

import bsuir.dc.rest.entity.Label
import org.springframework.stereotype.Repository

@Repository
class LabelInMemoryRepository: InMemoryRepository<Label>()