package org.example.radicalmotor.Services;

import jakarta.transaction.Transactional;
import org.example.radicalmotor.Constants.Provider;
import org.example.radicalmotor.Constants.RoleType;
import org.example.radicalmotor.Entities.Role;
import org.example.radicalmotor.Entities.User;
import org.example.radicalmotor.Repositories.IRoleRepository;
import org.example.radicalmotor.Repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private IUserRepository iUserRepository;

    @Autowired
    private IRoleRepository iRoleRepository;

    private static final Logger logger = LoggerFactory.getLogger(OAuth2UserService.class);

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        String email = oauth2User.getAttribute("email");
        logger.debug("OAuth2User email: {}", email);

        Optional<User> existingUserOpt = iUserRepository.findByEmail(email);
        User user;
        if (existingUserOpt.isPresent()) {
            logger.info("User with email {} already exists.", email);
            user = existingUserOpt.get();
        } else {
            logger.info("Saving new OAuth2 user with email: {}", email);
            user = new User();
            user.setEmail(email);
            user.setUsername(oauth2User.getName());
            user.setPassword(new BCryptPasswordEncoder().encode("default_password"));
            user.setProvider(String.valueOf(Provider.GOOGLE));

            Role userRole = iRoleRepository.findByName(RoleType.USER).orElseGet(() -> {
                Role newRole = new Role();
                newRole.setName(RoleType.USER);
                iRoleRepository.save(newRole);
                return newRole;
            });
            user.setRoles(Collections.singleton(userRole));
            logger.debug("Saving new user with roles: {}", user.getRoles());
            iUserRepository.save(user);
        }

        Set<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                .collect(Collectors.toSet());
        logger.info("User Authorities: {}", authorities);

        OAuth2AuthenticationToken authentication = new OAuth2AuthenticationToken(
                new DefaultOAuth2User(
                        authorities,
                        oauth2User.getAttributes(),
                        "email"
                ),
                authorities,
                userRequest.getClientRegistration().getRegistrationId()
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new DefaultOAuth2User(authorities, oauth2User.getAttributes(), "email");
    }
}