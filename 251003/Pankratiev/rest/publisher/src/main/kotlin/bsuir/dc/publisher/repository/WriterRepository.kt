package bsuir.dc.publisher.repository

import bsuir.dc.publisher.entity.Writer
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WriterRepository : JpaRepository<Writer, Long> {
    fun findByLoginIgnoreCase(login: String): Writer?
}