package org.example.radicalmotor.Configs;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.radicalmotor.Entities.User;
import org.example.radicalmotor.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;
import java.util.Optional;

@Configuration
public class HandlerFailureLogin extends SimpleUrlAuthenticationFailureHandler{
    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {

        String username = request.getParameter("username");
        Optional<User> userOpt = userService.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            userService.updateFailCount(user);
            int attemptsLeft = 3 - user.getCountFail();
            if (attemptsLeft <= 0) {
                request.getSession().setAttribute("errorMessage", "Your account is locked. Please wait 15 minutes before trying again.");
            } else {
                request.getSession().setAttribute("errorMessage", "Invalid username or password. Attempts left: " + attemptsLeft);
            }
        }

        super.setDefaultFailureUrl("/auth/login?error=true");
        super.onAuthenticationFailure(request, response, exception);
    }
}
