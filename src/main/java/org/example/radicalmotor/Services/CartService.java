package org.example.radicalmotor.Services;

import org.example.radicalmotor.Entities.Cart;
import org.example.radicalmotor.Entities.CartItem;
import org.example.radicalmotor.Entities.Vehicle;

import org.example.radicalmotor.Repositories.ICostTableRepository;
import org.example.radicalmotor.Repositories.IVehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    @Autowired
    private IVehicleRepository vehicleRepository;
    @Autowired
    private ICostTableRepository costTableRepository;
    @Autowired
    private ICostTableRepository userRepository;
    @Autowired
    private CostTableService costTableService;

    public Cart getCart(HttpSession session) {
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
            session.setAttribute("cart", cart);
        }
        return cart;
    }

    public void addToCart(String chassisNumber,int quantity, HttpSession session) {
        Cart cart = getCart(session);
        Vehicle vehicle = vehicleRepository.findByChassisNumberWithImages(chassisNumber).orElseThrow();
        double price = costTableService.getBaseCostByChassisNumber(chassisNumber);
        cart.getCartItems().add(new CartItem(vehicle.getChassisNumber(), vehicle.getVehicleName(), price, quantity, vehicle));
        calculateTotals(session);
        session.setAttribute("cart", cart);
    }

    public void removeFromCart(String chassisNumber, HttpSession session) {
        Cart cart = getCart(session);
        cart.getCartItems().removeIf(item -> item.getChassisNumber().equals(chassisNumber));
        calculateTotals(session);
        session.setAttribute("cart", cart);
    }

    public void updateCart(String chassisNumber, int quantity, HttpSession session) {
        Cart cart = getCart(session);
        for (CartItem item : cart.getCartItems()) {
            if (item.getChassisNumber().equals(chassisNumber)) {
                item.setQuantity(quantity);
                calculateTotals(session);
                session.setAttribute("cart", cart);
                return;
            }
        }
    }

    public void clearCart(HttpSession session) {
        Cart cart = getCart(session);
        cart.getCartItems().clear();
        calculateTotals(session);
        session.setAttribute("cart", cart);
    }

    public double getItemPrice(String chassisNumber, HttpSession session) {
        return costTableService.getBaseCostByChassisNumber(chassisNumber);
    }

    public double getSumPrice(HttpSession session) {
        Cart cart = getCart(session);
        calculateTotals(session);
        return cart.getTotalPrice();
    }

    public void calculateTotals(HttpSession session) {
        Cart cart = getCart(session);
        double total = 0;
        int quantity = 0;
        for (CartItem item : cart.getCartItems()) {
            total += costTableService.getBaseCostByChassisNumber(item.getChassisNumber()) * item.getQuantity();
            quantity += item.getQuantity();
        }
        cart.setTotalPrice(total);
        cart.setTotalQuantity(quantity);
    }
}
