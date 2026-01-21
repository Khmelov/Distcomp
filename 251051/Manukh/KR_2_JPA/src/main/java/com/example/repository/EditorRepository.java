package com.example.repository;

import com.example.entity.Editor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EditorRepository extends JpaRepository<Editor, Long> {

    Optional<Editor> findByLogin(String login);

    boolean existsByLogin(String login);

    boolean existsByLoginAndIdNot(String login, Long id);

    @Query("SELECT e FROM Editor e WHERE LOWER(e.login) LIKE LOWER(CONCAT('%', :login, '%'))")
    Page<Editor> findByLoginContainingIgnoreCase(@Param("login") String login, Pageable pageable);

    @Query("SELECT e FROM Editor e LEFT JOIN FETCH e.stories WHERE e.id = :id")
    Optional<Editor> findByIdWithStories(@Param("id") Long id);

    @Query("SELECT e FROM Editor e WHERE " +
            "(:login IS NULL OR LOWER(e.login) LIKE LOWER(CONCAT('%', :login, '%'))) AND " +
            "(:firstName IS NULL OR LOWER(e.firstname) LIKE LOWER(CONCAT('%', :firstName, '%'))) AND " +
            "(:lastName IS NULL OR LOWER(e.lastname) LIKE LOWER(CONCAT('%', :lastName, '%')))")
    Page<Editor> searchEditors(
            @Param("login") String login,
            @Param("firstName") String firstName,
            @Param("lastName") String lastName,
            Pageable pageable);
}