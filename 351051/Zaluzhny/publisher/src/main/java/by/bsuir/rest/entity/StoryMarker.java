package by.bsuir.rest.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tbl_story_marker", schema = "distcomp", uniqueConstraints = @UniqueConstraint(
        name = "uq_tbl_story_marker_pair",
        columnNames = {"story_id", "marker_id"}
))
public class StoryMarker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "story_id", nullable = false)
    private Story story;

    @Column(name = "marker_id", nullable = false)
    private Long markerId;

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof StoryMarker marker)) {
            return false;
        }
        return markerId != null && markerId.equals(marker.markerId);
    }

    @Override
    public int hashCode() {
        return markerId == null ? 0 : markerId.hashCode();
    }
}
