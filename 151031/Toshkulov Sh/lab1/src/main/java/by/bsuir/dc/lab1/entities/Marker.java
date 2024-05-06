package by.bsuir.dc.lab1.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Marker {
    private BigInteger id;
    private String name;

}
