package org.ex.distributed_computing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post extends AbstractEntity {

    @ManyToOne
    private Tweet tweet;

    @Column(name = "content", nullable = false, length = 2048)
    private String content;
}
