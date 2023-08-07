package com.example.memepage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collections;


@Configuration
@EnableWebSecurity
public class SecurityConfig {


    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;

    @Value("${jwt.auth.converter.principal-attribute}")
    private String principalAttribute;
    private final KeycloakLogoutHandler keycloakLogoutHandler;

    SecurityConfig(KeycloakLogoutHandler keycloakLogoutHandler) {
        this.keycloakLogoutHandler = keycloakLogoutHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(
                        authorizeHttpRequests ->
                                authorizeHttpRequests
                                        .requestMatchers(HttpMethod.GET, "/login").permitAll()
                                        .requestMatchers("/oauth2/**").permitAll()
                                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2Login -> oauth2Login.loginPage("/login").defaultSuccessUrl("/", true))
                .logout(logout -> logout.addLogoutHandler(keycloakLogoutHandler).logoutSuccessUrl("/login").permitAll())
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public OAuth2UserService<OidcUserRequest, OidcUser> oAuth2UserService(@Autowired JwtDecoder jwtDecoder) {
        OidcUserService delegate = new OidcUserService();
        return (userRequest) -> {
            OidcUser oidcUser = delegate.loadUser(userRequest);

            String nameAttributeKey = principalAttribute == null ? JwtClaimNames.SUB : principalAttribute;
            return new DefaultOidcUser(Collections.emptyList(), oidcUser.getIdToken(), oidcUser.getUserInfo(), nameAttributeKey);
        };
    }


}