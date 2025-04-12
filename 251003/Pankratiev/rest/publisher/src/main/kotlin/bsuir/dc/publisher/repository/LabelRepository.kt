package bsuir.dc.publisher.repository

import bsuir.dc.publisher.entity.Label
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface LabelRepository : JpaRepository<Label, Long> {
}
