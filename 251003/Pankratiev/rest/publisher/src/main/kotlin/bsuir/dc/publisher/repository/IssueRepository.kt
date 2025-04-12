package bsuir.dc.publisher.repository

import bsuir.dc.publisher.entity.Issue
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface IssueRepository : JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {
    @Query(
        """
        SELECT DISTINCT i 
        FROM Issue i 
        JOIN i.labels l 
        WHERE l.id IN :labelIds 
        GROUP BY i 
        HAVING COUNT(DISTINCT l.id) = :labelCount
        """
    )
    fun findByAllLabelIds(@Param("labelIds") labelIds: List<Long>, @Param("labelCount") labelCount: Long): List<Issue>

    @Query(
        """
        SELECT DISTINCT i 
        FROM Issue i 
        JOIN i.labels l 
        WHERE l.name IN :labelNames 
        GROUP BY i 
        HAVING COUNT(DISTINCT l.name) = :labelCount
        """
    )
    fun findByAllLabelNames(@Param("labelNames") labelNames: List<String>, @Param("labelCount") labelCount: Long): List<Issue>
}
