package com.dc.anotherone.model.blo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="tbl_message")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "newsId")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private News news;

    @Column(length = 2048, nullable = false)
    @Size(min = 2, max = 2048)
    private String content;
}
