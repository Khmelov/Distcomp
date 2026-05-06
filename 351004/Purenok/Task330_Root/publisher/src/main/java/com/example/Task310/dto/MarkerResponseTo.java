package com.example.Task310.dto;

import jakarta.persistence.*;
import lombok.*;
import java.util.Set;

@Entity
@Table(name = "tbl_marker", schema = "distcomp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
<<<<<<< HEAD:351004/Purenok/Task330_Root/publisher/src/main/java/com/example/Task310/dto/MarkerResponseTo.java
public class MarkerResponseTo {


    
    private Long id;

=======
public class Marker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 32)
>>>>>>> e9b46436b12a679a1122bfd9ca7840c196ff410a:351004/Purenok/Task310/src/main/java/com/example/Task310/bean/Marker.java
    private String name;

    @ManyToMany(mappedBy = "markers")
    private Set<Story> stories;
}