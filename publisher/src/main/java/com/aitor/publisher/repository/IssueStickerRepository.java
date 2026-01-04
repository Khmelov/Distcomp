package com.aitor.publisher.repository;

import com.aitor.publisher.model.Issue;
import com.aitor.publisher.model.IssueSticker;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IssueStickerRepository extends JpaRepository<IssueSticker, Long> {
    List<IssueSticker> findByIssueId(@NonNull Issue issueId);
}
