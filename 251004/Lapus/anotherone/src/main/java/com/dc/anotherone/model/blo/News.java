package com.dc.anotherone.model.blo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tbl_news")
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "authorId")
    private Author author;

    @Column(length = 64, nullable = false)
    @Size(min = 2, max = 64)
    private String title;

    @Column(length = 2048, nullable = false)
    @Size(min = 4, max = 2048)
    private String content;

    @Column(nullable = false)
    private Timestamp created;

    @Column(nullable = false)
    private Timestamp modified;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "m2m_marker_news",
            joinColumns = {@JoinColumn(name = "newsId")},
            inverseJoinColumns = {@JoinColumn(name = "markerId")}
    )

    private List<Marker> markers;
}
