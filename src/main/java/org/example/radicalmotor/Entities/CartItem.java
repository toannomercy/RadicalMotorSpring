package org.example.radicalmotor.Entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CartItem {
    private String chassisNumber;
    private String vehicleName;
    private double price;
    private int quantity;
    private Vehicle vehicle;
}
