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
@Table(name = "tbl_notice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notice extends AbstractEntity{

  @ManyToOne
  private News news;

  @Column(name = "content")
  private String content;
}
