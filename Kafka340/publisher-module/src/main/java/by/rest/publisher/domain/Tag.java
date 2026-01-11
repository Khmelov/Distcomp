package by.rest.publisher.domain;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_tag")
public class Tag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 32)
    private String name;
    
    @ManyToMany(mappedBy = "tags")
    private Set<Story> stories = new HashSet<>();
    
    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public Set<Story> getStories() { return stories; }
    public void setStories(Set<Story> stories) { this.stories = stories; }
}