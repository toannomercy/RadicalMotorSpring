package org.example.radicalmotor.Controllers;

import org.example.radicalmotor.Entities.Cart;
import org.example.radicalmotor.Services.CartService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.example.radicalmotor.Services.CostTableService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping("/cart")
    public String showCart(HttpSession session, Model model) {
        Cart cart = cartService.getCart(session);
        model.addAttribute("cart", cart);
        model.addAttribute("cartItem", cart.getCartItems());
        model.addAttribute("totalPrice", cart.getTotalPrice());
        model.addAttribute("totalQuantity", cart.getTotalQuantity());
        return "product/cart";
    }

    @PostMapping("/add-to-cart")
    public String addToCart(@RequestParam String chassisNumber, @RequestParam int quantity, HttpSession session) {
        cartService.addToCart(chassisNumber, quantity, session);
        return "redirect:/cart";
    }

    @PostMapping("/remove")
    public String removeFromCart(@RequestParam String chassisNumber, HttpSession session) {
        cartService.removeFromCart(chassisNumber, session);
        return "redirect:/cart";
    }

    @PostMapping("/cart/updateCart/{chassisNumber}/{quantity}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCart(
            @PathVariable String chassisNumber,
            @PathVariable int quantity,
            HttpSession session) {
        cartService.updateCart(chassisNumber, quantity, session);
        Cart cart = cartService.getCart(session);
        double itemPrice = cartService.getItemPrice(chassisNumber, session);
        Map<String, Object> response = new HashMap<>();
        response.put("price", itemPrice);
        response.put("totalQuantity", cart.getTotalQuantity());
        response.put("totalPrice", cart.getTotalPrice());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cart/clearCart")
    public String clearCart(HttpSession session) {
        cartService.clearCart(session);
        return "redirect:/cart";
    }

//    @PostMapping("/checkout")
//    public String checkout(HttpSession session, Model model) {
//        cartService.saveCart(session);
//        Cart cart = cartService.getCart(session);
//        model.addAttribute("cart", cart);
//        model.addAttribute("totalPrice", cart.getTotalPrice());
//        return "redirect:/checkout";
//    }
//
//    @GetMapping("/checkout")
//    public String getCheckout(HttpSession session, Model model) {
//        Cart cart = cartService.getCart(session);
//        model.addAttribute("cart", cart);
//        model.addAttribute("totalPrice", cart.getTotalPrice());
//        return "product/checkout";
//    }
//
//    @PostMapping("/confirm-checkout")
//    public String confirmCheckout(HttpSession session, Model model) {
//        cartService.saveCart(session);
//        model.addAttribute("message", "Checkout successful!");
//        return "product/confirmation";
//    }
}
