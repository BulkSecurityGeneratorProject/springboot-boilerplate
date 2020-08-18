package com.lagab.boilerplate.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.lagab.boilerplate.common.security.Authorities;
import com.lagab.boilerplate.common.security.jwt.JWTConfigurer;
import com.lagab.boilerplate.common.security.jwt.TokenProvider;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration {
    @Configuration
    @Order(2)
    public static class UserJwtSecurityConfiguration extends WebSecurityConfigurerAdapter {
        private final AuthenticationManagerBuilder authenticationManagerBuilder;
        //private final UserDetailsService userDetailsService;
        private final TokenProvider tokenProvider;
        //private final SecurityProblemSupport problemSupport;

        public UserJwtSecurityConfiguration(AuthenticationManagerBuilder authenticationManagerBuilder, TokenProvider tokenProvider) {
            this.authenticationManagerBuilder = authenticationManagerBuilder;
            //this.userDetailsService = userDetailsService;
            this.tokenProvider = tokenProvider;
        }

        @Override
        @Bean
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }
        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring()
               .antMatchers(HttpMethod.OPTIONS, "/**")
               .antMatchers("/swagger-ui/index.html")
               .antMatchers("/test/**");
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                    .csrf()
                    .disable()
                    //.addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class)
                    .addFilter(corsFilter())
                    .exceptionHandling()
                    .and()
                    .headers()
                    .contentSecurityPolicy("default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:")
                    .and()
                    .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                    .and()
                    .featurePolicy("geolocation 'none'; midi 'none'; sync-xhr 'none'; microphone 'none'; camera 'none'; magnetometer 'none'; gyroscope 'none'; speaker 'none'; fullscreen 'self'; payment 'none'")
                    .and()
                    .frameOptions()
                    .deny()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/auth/authenticate").permitAll()
                    .antMatchers("/auth/register").permitAll()
                    .antMatchers("/auth/activate").permitAll()
                    .antMatchers("/account/reset-password/init").permitAll()
                    .antMatchers("/account/reset-password/finish").permitAll()
                    .antMatchers("/api/**").authenticated()
                    .antMatchers("/actuator/health").permitAll()
                    .antMatchers("/actuator/info").permitAll()
                    .antMatchers("/actuator/prometheus").permitAll()
                    .antMatchers("/actuator/**").hasAuthority(Authorities.ADMIN)
                    .and()
                    .httpBasic()
                    .and()
                    .apply(securityConfigurerAdapter());
            // @formatter:on
        }

        public CorsFilter corsFilter() {
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            CorsConfiguration config = new CorsConfiguration();
            config.setAllowCredentials(true);
            config.addAllowedOrigin("*");
            config.addAllowedHeader("*");
            config.addAllowedMethod("*");
            source.registerCorsConfiguration("/**", config);
            source.registerCorsConfiguration("/api/**", config);
            source.registerCorsConfiguration("/auth/**", config);
            source.registerCorsConfiguration("/actuator/**", config);
            source.registerCorsConfiguration("/v2/api-docs", config);
            source.registerCorsConfiguration("/browser/**", config);
            return new CorsFilter(source);
        }

        private JWTConfigurer securityConfigurerAdapter() {
            return new JWTConfigurer(tokenProvider);
        }

    }
    @Configuration
    @Order(1)
    public static class SystemSecurityConfiguration extends WebSecurityConfigurerAdapter {

        @Override
        public void configure(WebSecurity web) throws Exception {
            web.ignoring()
               .antMatchers(HttpMethod.OPTIONS, "/**")
               .antMatchers("/swagger-ui/index.html")
               .antMatchers("/test/**");
        }

        @Override
        public void configure(HttpSecurity http) throws Exception {
            // @formatter:off
            http
                    .csrf()
                    .disable()
                    //.addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class)
                    .exceptionHandling()
                    .and()
                    .headers()
                    .contentSecurityPolicy("default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:")
                    .and()
                    .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
                    .and()
                    .featurePolicy("geolocation 'none'; midi 'none'; sync-xhr 'none'; microphone 'none'; camera 'none'; magnetometer 'none'; gyroscope 'none'; speaker 'none'; fullscreen 'self'; payment 'none'")
                    .and()
                    .frameOptions()
                    .deny()
                    .and()
                    .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                    .authorizeRequests()
                    .antMatchers("/auth/authenticate").permitAll()
                    .antMatchers("/auth/register").permitAll()
                    .antMatchers("/auth/activate").permitAll()
                    .antMatchers("/account/reset-password/init").permitAll()
                    .antMatchers("/account/reset-password/finish").permitAll()
                    .antMatchers("/api/**").authenticated()
                    .antMatchers("/actuator/health").permitAll()
                    .antMatchers("/actuator/info").permitAll()
                    .antMatchers("/actuator/prometheus").permitAll()
                    .antMatchers("/actuator/**").hasAuthority(Authorities.ADMIN)
                    .and()
                    .httpBasic();
            // @formatter:on
        }
    }








}
