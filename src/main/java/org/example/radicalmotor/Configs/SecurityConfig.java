package org.example.radicalmotor.Configs;

import org.example.radicalmotor.Constants.RoleType;
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
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.session.HttpSessionEventPublisher;
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
    private ClientRegistrationRepository clientRegistrationRepository;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**","/scss/**","/Theme/**","/img/**","/images/profiles/**","/jquery/**","/lib/**", "/oauth2/**", "/auth/register", "/auth/login", "/error").permitAll()
                        .requestMatchers("/admin/**").hasAnyAuthority(String.valueOf(RoleType.ADMIN))
//                        .requestMatchers("/vehicles/edit/**", "/vehicles/delete", "/vehicles/add", "/vehicle-type/add", "/vehicle-type", "/vehicle-type/edit", "/vehicle-type/delete").hasAnyAuthority(String.valueOf(RoleType.ADMIN)))
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
                        .loginProcessingUrl("/login")
                        .defaultSuccessUrl("/")
                        .failureUrl("/users/login?error=true")
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
        return new HandlerOAuth2SuccessLogin();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    @Bean
    public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
        SimpleAuthorityMapper authorityMapper = new SimpleAuthorityMapper();
        authorityMapper.setConvertToUpperCase(true);
        authorityMapper.setPrefix("");
        return authorityMapper;
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SpringSecurityDialect springSecurityDialect() {
        return new SpringSecurityDialect();
    }
}
