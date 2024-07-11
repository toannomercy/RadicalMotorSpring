package org.example.radicalmotor.Configs;

import org.example.radicalmotor.Constants.RoleType;
import org.example.radicalmotor.Entities.User;
import org.example.radicalmotor.Services.OAuth2UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.radicalmotor.Services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class HandlerSuccessLogin extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private OAuth2UserService OAuth2UserService;
    @Autowired
    private UserService userService;
    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;
    private static final Logger logger = LoggerFactory.getLogger(HandlerSuccessLogin.class);

    public HandlerSuccessLogin() {
        setDefaultTargetUrl("/");
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            handleOAuth2Success(request, response, (OAuth2AuthenticationToken) authentication);
        } else {
            handleRegularSuccess(request, response, authentication);
        }
    }

    public void handleOAuth2Success(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
        String email = authToken.getPrincipal().getAttribute("email");
        String name = authToken.getPrincipal().getAttribute("name");

        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                authToken.getAuthorizedClientRegistrationId(), authToken.getName());

        if (authorizedClient != null) {
            OAuth2UserRequest userRequest = new OAuth2UserRequest(
                    authorizedClient.getClientRegistration(),
                    authorizedClient.getAccessToken(),
                    authToken.getPrincipal().getAttributes()
            );

            OAuth2UserService.loadUser(userRequest);
        } else {
            logger.error("OAuth2AuthorizedClient is null");
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private void handleRegularSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        Optional<User> optionalUser = userService.findByUsername(username);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();


            if (!user.isAccountNonLocked()) {
                request.getSession().setAttribute("errorMessage", "Your account is locked. You have to wait 15 minutes");
                response.sendRedirect("/auth/login?error=true");
                return;
            }

            if (user.getLockExpired() != null) {
                if (user.getLockExpired().getTime() < System.currentTimeMillis()) {
                    userService.resetLoginFail(user);
                }
            } else {
                userService.resetLoginFail(user);
            }

            UserDetails userDetails = userService.loadUserByUsername(username);
            UsernamePasswordAuthenticationToken newAuth = new UsernamePasswordAuthenticationToken(userDetails, authentication.getCredentials(), userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(newAuth);

            boolean isAdmin = userDetails.getAuthorities().contains(new SimpleGrantedAuthority(String.valueOf(RoleType.ADMIN)));

            if (isAdmin) {
                setDefaultTargetUrl("/admin");
            } else {
                setDefaultTargetUrl("/");
            }
        } else {
            logger.error("User not found");
            setDefaultTargetUrl("/auth/login?error=true");
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
