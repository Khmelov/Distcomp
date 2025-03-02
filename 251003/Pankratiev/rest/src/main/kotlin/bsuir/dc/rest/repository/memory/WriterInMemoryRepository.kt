package bsuir.dc.rest.repository.memory

import bsuir.dc.rest.entity.Writer
import org.springframework.stereotype.Repository

@Repository
class WriterInMemoryRepository: InMemoryRepository<Writer>()