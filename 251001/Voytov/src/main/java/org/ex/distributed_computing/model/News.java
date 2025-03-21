package org.ex.distributed_computing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_news")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class News extends AbstractEntity{

  @ManyToOne
  private Author author;

  @Column(name = "title", unique = true, nullable = false)
  private String title;

  @Column(name = "content")
  private String content;

  private LocalDateTime createdDateTime;

  private LocalDateTime updatedDateTime;
}
