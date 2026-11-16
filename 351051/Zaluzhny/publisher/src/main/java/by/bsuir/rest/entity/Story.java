package by.bsuir.rest.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

import java.time.Instant;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "tbl_story", schema = "distcomp")
public class Story implements Identifiable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, unique = true, length = 64)
    private String title;

    @Column(nullable = false, length = 2048)
    private String content;

    @Column(nullable = false)
    private Instant created;

    @Column(nullable = false)
    private Instant modified;

    @OneToMany(mappedBy = "story", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @BatchSize(size = 32)
    @Setter(AccessLevel.NONE)
    private Set<StoryMarker> markers = new LinkedHashSet<>();

    public Set<Long> getMarkerIds() {
        return markers.stream()
                .map(StoryMarker::getMarkerId)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public void setMarkerIds(Set<Long> markerIds) {
        Set<Long> desired = markerIds == null ? Set.of() : markerIds;
        markers.removeIf(link -> link.getMarkerId() == null || !desired.contains(link.getMarkerId()));
        Set<Long> current = markers.stream()
                .map(StoryMarker::getMarkerId)
                .collect(Collectors.toSet());
        for (Long markerId : desired) {
            if (markerId == null || !current.add(markerId)) {
                continue;
            }
            StoryMarker link = new StoryMarker();
            link.setStory(this);
            link.setMarkerId(markerId);
            markers.add(link);
        }
    }
}
