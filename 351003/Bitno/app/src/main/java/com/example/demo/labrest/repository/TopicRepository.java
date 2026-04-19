package com.example.demo.labrest.repository;

import com.example.demo.labrest.model.Creator;
import com.example.demo.labrest.model.Marker;
import com.example.demo.labrest.model.Topic;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TopicRepository extends BaseRepository<Topic, Long> {
    List<Topic> findByCreator_Id(Long creatorId);
    long countByMarkersContains(Marker marker);
    boolean existsByCreatorAndTitle(Creator creator, String title);

    @Query("SELECT t FROM Topic t LEFT JOIN t.markers m WHERE " +
            "(:markerNames IS NULL OR m.name IN :markerNames) AND " +
            "(:markerIds IS NULL OR m.id IN :markerIds) AND " +
            "(:creatorLogin IS NULL OR t.creator.login = :creatorLogin) AND " +
            "(:title IS NULL OR t.title LIKE %:title%) AND " +
            "(:content IS NULL OR t.content LIKE %:content%)")
    List<Topic> findByFilters(@Param("markerNames") List<String> markerNames,
                              @Param("markerIds") List<Long> markerIds,
                              @Param("creatorLogin") String creatorLogin,
                              @Param("title") String title,
                              @Param("content") String content);
}