package org.example.radicalmotor.Controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.radicalmotor.Constants.RoleType;
import org.example.radicalmotor.Entities.Role;
import org.example.radicalmotor.Entities.User;
import org.example.radicalmotor.Services.RoleService;
import org.example.radicalmotor.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Set;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String usernameOrEmail, @RequestParam String password, HttpSession session, Model model) {
        User user = userService.authenticate(usernameOrEmail, password);
        if (user != null) {
            session.setAttribute("userId", user.getId());
            return "redirect:/";
        } else {
            model.addAttribute("error", "Invalid username/email or password");
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") User user, Model model) {
        if (userService.save(user)) {
            return "redirect:/auth/login";
        } else {
            model.addAttribute("error", "Registration failed.");
            return "auth/register";
        }
    }
}