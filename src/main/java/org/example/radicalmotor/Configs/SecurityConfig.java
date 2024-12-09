package org.example.radicalmotor.Configs;

import org.example.radicalmotor.Constants.RoleType;
import org.example.radicalmotor.Entities.Cart;
import org.example.radicalmotor.Services.OAuth2UserService;
import org.example.radicalmotor.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.context.annotation.SessionScope;
import org.thymeleaf.extras.springsecurity6.dialect.SpringSecurityDialect;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    @Autowired
    private OAuth2UserService oAuth2UserService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private HandlerFailureLogin handlerFailureLogin;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**","/scss/**","/Theme/**","/img/**","/images/profiles/**","/jquery/**","/lib/**", "/oauth2/**", "/auth/register/**", "/auth/login/**", "/error/**", "/auth/forgotPassword/**", "/auth/resetPassword/**").permitAll()
                        .requestMatchers("/admin/**").hasAnyAuthority(String.valueOf(RoleType.ADMIN))
                        .requestMatchers("/products", "/products/detail").hasAnyAuthority(String.valueOf(RoleType.USER), String.valueOf(RoleType.ADMIN))
                        .requestMatchers("/").permitAll()
                        .requestMatchers("/api/**").hasAnyAuthority(String.valueOf(RoleType.USER), String.valueOf(RoleType.ADMIN))
                        .anyRequest().authenticated())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/auth/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(oAuth2UserService)
                        )
                        .failureUrl("/auth/login?error=true")
                        .successHandler(successHandler())
                )
                .formLogin(formLogin -> formLogin
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .defaultSuccessUrl("/")
                        .failureUrl("/auth/login?error=true")
                        .successHandler(successHandler())
                        .failureHandler(handlerFailureLogin)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/auth/login?logout")
                        .deleteCookies("JSESSIONID")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll())
                .rememberMe(rememberMe -> rememberMe
                        .key("uniqueAndSecret")
                        .rememberMeCookieName("remember-me")
                        .tokenValiditySeconds(24 * 60 * 60)
                        .userDetailsService(userService))
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .invalidSessionUrl("/auth/login?session=invalid"))
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedPage("/error"))
                .httpBasic(httpBasic -> httpBasic.realmName("RadicalMotor"));

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new HandlerSuccessLogin();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }

    @Bean
    @SessionScope
    public Cart cart() {
        return new Cart();
    }
}
