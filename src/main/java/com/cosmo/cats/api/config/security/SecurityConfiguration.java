package com.cosmo.cats.api.config.security;

import static com.cosmo.cats.api.util.SecurityUtil.ROLE_CLAIMS_HEADER;
import static org.springframework.security.config.Customizer.withDefaults;
import static org.springframework.security.web.csrf.CookieCsrfTokenRepository.withHttpOnlyFalse;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.web.HeaderBearerTokenResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class SecurityConfiguration {

  private static final String API_V_1_ORDERS = "/api/v1/orders/**";
  private static final String API_V_1_PRODUCTS = "/api/v1/products/**";

  @Bean
  @Order(1)
  public SecurityFilterChain filterChainOrdersV1(HttpSecurity http)
      throws Exception {
    http.securityMatcher(API_V_1_ORDERS)
        .cors(withDefaults())
        .csrf(CsrfConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize -> authorize.requestMatchers(antMatcher(API_V_1_ORDERS)).authenticated())
        .oauth2ResourceServer(oauth2 -> oauth2.jwt(withDefaults()));

    return http.build();
  }

  @Bean
  @Order(2)
  public SecurityFilterChain filterChainProductsV1(HttpSecurity http, JwtDecoder decoder)
      throws Exception {

    JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
    jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new AuthorityConverter());

    http.securityMatcher(API_V_1_PRODUCTS)
        .cors(withDefaults())
        .csrf(csrf -> csrf.csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
            .csrfTokenRepository(withHttpOnlyFalse()))
        .addFilterBefore(new ProductApiKeyFilter(decoder),
            UsernamePasswordAuthenticationFilter.class)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize ->
            authorize.requestMatchers(antMatcher(API_V_1_PRODUCTS)).authenticated())
        .oauth2ResourceServer(oAuth2 ->
            oAuth2.bearerTokenResolver(new HeaderBearerTokenResolver(ROLE_CLAIMS_HEADER))
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)));
    return http.build();
  }
}
