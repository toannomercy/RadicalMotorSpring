package org.example.radicalmotor.Configs;

import org.example.radicalmotor.Services.OAuth2UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.annotations.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
@Component
public class HandlerOAuth2SuccessLogin extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private OAuth2UserService OAuth2UserService;

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;

    public HandlerOAuth2SuccessLogin() {
        setDefaultTargetUrl("/");
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
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
}
