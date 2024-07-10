package org.example.radicalmotor.Controllers;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.radicalmotor.Constants.Provider;
import org.example.radicalmotor.Constants.RoleType;
import org.example.radicalmotor.Entities.Role;
import org.example.radicalmotor.Entities.User;
import org.example.radicalmotor.Services.MailService;
import org.example.radicalmotor.Services.RoleService;
import org.example.radicalmotor.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private MailService mailService;

    @GetMapping("/login")
    public String showLoginPage(Model model, HttpSession session) {
        Object errorMessage = session.getAttribute("errorMessage");
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            session.removeAttribute("errorMessage");
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        try {
            User user = userService.authenticate(username, password);
            if (user != null) {
                session.setAttribute("userId", user.getId());
                return "redirect:/";
            } else {
                model.addAttribute("errorMessage", "Invalid username or password.");
                return "auth/login";
            }
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "auth/login";
        }
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") User user, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("errors", bindingResult.getAllErrors());
            return "auth/register";
        }
        Set<Role> roles = new HashSet<>();
        Role userRole = roleService.findByName(RoleType.USER);
        if (userRole != null) {
            roles.add(userRole);
        }
        user.setRoles(roles);
        userService.save(user);
        return "redirect:/auth/login";
    }

    @GetMapping("/forgotPassword")
    public String showForgotPasswordPage() {
        return "auth/forgotPassword";
    }

    @PostMapping("/forgotPassword")
    public String ForgotPassword(@RequestParam("email") String email, Model model){
        Optional<User> userOptional  = userService.getUserByEmail(email);
        if(userOptional .isPresent()){
            User user = userOptional.get();
            if (Provider.GOOGLE.equals(user.getProvider())){
                model.addAttribute("errorMessage", "You cannot reset the password for a Google account.");
                return "auth/forgotPassword";
            }
            String token = userService.GenTokenResetPassword(user);
            String url = "http://localhost:8080/auth/resetPassword?token="+token;
            mailService.sendMail(user.getEmail(), url);
            model.addAttribute("successMessage", "A reset link has been sent to your email.");
        } else {
            model.addAttribute("errorMessage", "Email address not found.");
        }
        return "redirect:/auth/forgotPassword";
    }

    @GetMapping("/resetPassword")
    public String showResetPasswordPage(@RequestParam("token") String token, Model model) {
        Optional<User> userOpt = userService.getUserByResetToken(token);
        if (userOpt.isPresent()) {
            model.addAttribute("token", token);
            return "auth/resetPassword";
        } else {
            model.addAttribute("errorMessage", "Invalid or expired password reset token.");
            return "auth/login";
        }
    }

    @PostMapping("/resetPassword")
    public String resetPassword(@RequestParam("password") String password,
                                @RequestParam("confirmPassword") String confirmPassword,
                                @RequestParam("token") String token, Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("errorMessage", "Passwords do not match.");
            model.addAttribute("token", token);
            return "auth/resetPassword";
        }

        Optional<User> userOpt = userService.getUserByResetToken(token);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            userService.updatePassword(user, password);
            userService.ResetDateForgotPassword(user);
            model.addAttribute("successMessage", "Your password has been successfully reset.");
            return "redirect:/auth/login";
        } else {
            model.addAttribute("errorMessage", "Invalid or expired password reset token.");
        }
        return "auth/resetPassword";
    }

}