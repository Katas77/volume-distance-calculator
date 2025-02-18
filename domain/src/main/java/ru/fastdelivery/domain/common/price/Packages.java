package ru.fastdelivery.domain.common.price;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Packages {

    private int length;
    private int width;
    private int height;


}
