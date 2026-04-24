package by.bsuir.task340.publisher.repository;

import by.bsuir.task340.publisher.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long>, JpaSpecificationExecutor<Tag> {
    Optional<Tag> findByName(String name);

    List<Tag> findByNameIn(Collection<String> names);

    @Query("select count(tw) from Tweet tw join tw.tags tag where tag.id = :tagId")
    long countTweetsUsingTag(@Param("tagId") Long tagId);
}
