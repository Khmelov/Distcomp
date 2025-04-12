package by.molchan.Discussion.repositories;


import by.molchan.Discussion.models.Comment;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CommentsRepository extends CrudRepository<Comment, Comment.CommentKey> {
    @Query("SELECT * FROM tbl_comment WHERE country = :country AND id = :id")
    Optional<Comment> findByCountryAndId(@Param("country") String country, @Param("id") Long id);

    @Query("DELETE FROM tbl_comment WHERE country = :country AND id = :id")
    void deleteByCountryAndId(@Param("country") String country, @Param("id") Long id);

}
