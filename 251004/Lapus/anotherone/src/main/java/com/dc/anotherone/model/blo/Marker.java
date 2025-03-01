package com.dc.anotherone.model.blo;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="tbl_marker")
public class Marker {

    public Marker(String name){
        this.name=name;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 32, nullable = false)
    @Size(min = 2, max = 32)
    private String name;
}
