package org.ex.distributed_computing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tbl_label")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Label extends AbstractEntity {

  @Column(name = "name", unique = true, nullable = false)
  private String name;
}
