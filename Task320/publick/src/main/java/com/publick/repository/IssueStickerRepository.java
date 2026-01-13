package com.publick.repository;

import com.publick.entity.IssueSticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IssueStickerRepository extends CrudRepository<IssueSticker, Long> {

    List<IssueSticker> findByIssueId(Long issueId);

    List<IssueSticker> findByStickerId(Long stickerId);

    @Modifying
    @Query("DELETE FROM IssueSticker iss WHERE iss.issue.id = :issueId")
    void deleteByIssueId(@Param("issueId") Long issueId);

    @Modifying
    @Query("DELETE FROM IssueSticker iss WHERE iss.sticker.id = :stickerId")
    void deleteByStickerId(@Param("stickerId") Long stickerId);

    boolean existsByIssueIdAndStickerId(Long issueId, Long stickerId);
}