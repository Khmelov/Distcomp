package by.bsuir.task310.repository;

import by.bsuir.task310.model.Label;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface LabelRepository extends JpaRepository<Label, Long> {

    @Transactional
    @Modifying
    void deleteByTopicId(Long topicId);
}