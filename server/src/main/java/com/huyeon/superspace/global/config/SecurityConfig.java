package com.huyeon.superspace.global.config;

import com.huyeon.superspace.global.jwt.JwtAccessDeniedHandler;
import com.huyeon.superspace.global.jwt.JwtAuthenticationEntryPoint;
import com.huyeon.superspace.global.jwt.JwtSecurityConfig;
import com.huyeon.superspace.global.jwt.TokenProvider;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@AllArgsConstructor
@EnableWebSecurity(debug = true)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //JWT 사용
                .csrf().disable()

                .httpBasic().disable()

                .formLogin(
                        login -> login.loginPage("/login")
                                .loginProcessingUrl("/auth/login")
                                .permitAll()
                                .defaultSuccessUrl("/workspace", false)
                                .failureUrl("/")
                )

                .addFilterBefore(corsFilter, UsernamePasswordAuthenticationFilter.class)

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                //JWT Exception Handling
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
                .accessDeniedPage("/access-denied")

                //Authorize Request
                .and()
                .authorizeHttpRequests()
                .antMatchers("/", "/**").permitAll()
                .anyRequest().authenticated()

                //JWT Config
                .and()
                .apply(new JwtSecurityConfig(tokenProvider))

                .and()
                .rememberMe();

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
