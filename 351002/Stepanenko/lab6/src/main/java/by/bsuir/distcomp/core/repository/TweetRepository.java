package by.bsuir.distcomp.core.repository;

import by.bsuir.distcomp.core.domain.Tweet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TweetRepository extends JpaRepository<Tweet, Long>, JpaSpecificationExecutor<Tweet> {

    boolean existsByTitle(String title);

    /**
     * Подсчитывает количество твитов, к которым привязан данный маркер.
     * Используется для удаления "осиротевших" маркеров.
     */
    long countByMarkersId(Long markerId);
}