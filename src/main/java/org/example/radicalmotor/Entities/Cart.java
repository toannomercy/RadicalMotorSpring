package org.example.radicalmotor.Entities;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Cart {

    private List<CartItem> cartItems = new ArrayList<>();
    private double totalPrice;
    private int totalQuantity;
}
