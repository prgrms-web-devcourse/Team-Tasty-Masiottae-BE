package com.tasty.masiottae.security.config;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.tasty.masiottae.account.repository.TokenCache;
import com.tasty.masiottae.security.filter.JwtAuthenticationFilter;
import com.tasty.masiottae.security.filter.JwtAuthorizationFilter;
import com.tasty.masiottae.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.firewall.DefaultHttpFirewall;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenCache tokenCache;
    private final CorsFilter corsFilter;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(
            tokenCache, jwtTokenProvider, authenticationManager);
        JwtAuthorizationFilter jwtAuthorizationFilter = new JwtAuthorizationFilter(
            tokenCache, jwtTokenProvider);
        jwtAuthenticationFilter.setFilterProcessesUrl("/login");

        http
            .csrf().disable()
            .formLogin().disable()
            .httpBasic().disable()
            .logout().disable()
            .sessionManagement().sessionCreationPolicy(STATELESS)
            .and()
            .authorizeRequests().antMatchers("/signup", "/login", "/logout", "/accounts/check", "/re-issue").permitAll()
            .and()
            .authorizeRequests().antMatchers("/accounts")
                .hasAnyAuthority("ROLE_ACCOUNT", "ROLE_MANAGER")
            .and()
            .authorizeRequests().antMatchers("/accounts/**")
                .hasAnyAuthority("ROLE_ACCOUNT")
            .anyRequest().authenticated()
            .and()
            .addFilter(corsFilter)
            .addFilter(jwtAuthenticationFilter)
            .addFilterBefore(jwtAuthorizationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public HttpFirewall defaultHttpFirewall() {
        return new DefaultHttpFirewall();
    }

}
