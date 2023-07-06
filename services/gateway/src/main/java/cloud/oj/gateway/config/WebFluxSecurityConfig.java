package cloud.oj.gateway.config;

import cloud.oj.gateway.filter.LoginFilter;
import cloud.oj.gateway.filter.TokenVerifyFilter;
import cloud.oj.gateway.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;

/**
 * Spring Security 配置
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebFluxSecurityConfig {

    private final UserService userService;

    private final ObjectMapper mapper;

    public WebFluxSecurityConfig(UserService userService, ObjectMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    private static class Role {
        private static final String USER = "USER";
        private static final String ADMIN = "ADMIN";
    }

    @Bean
    public LoginFilter loginFilter(ServerCodecConfigurer serverCodecConfigurer) {
        return new LoginFilter(authenticationManager(), serverCodecConfigurer, mapper);
    }

    @Bean
    public TokenVerifyFilter tokenVerifyFilter() {
        return new TokenVerifyFilter();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         ServerCodecConfigurer serverCodecConfigurer) {
        return http.requestCache(requestCacheSpec -> requestCacheSpec.requestCache(NoOpServerRequestCache.getInstance()))
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .logout(ServerHttpSecurity.LogoutSpec::disable)
                .httpBasic(httpBasicSpec -> httpBasicSpec.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)))
                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
                        .pathMatchers("/core/**").denyAll()
                        .pathMatchers("/judge/**").denyAll()
                        .pathMatchers(HttpMethod.GET, "/api/core/user/profile").permitAll()
                        .pathMatchers(HttpMethod.PUT, "/api/core/user/profile").hasRole(Role.USER)
                        .pathMatchers("/api/core/user/admin/**").hasRole(Role.ADMIN)
                        .pathMatchers("/api/core/problem/admin/**").hasRole(Role.ADMIN)
                        .pathMatchers("/api/core/solution/**").hasRole(Role.USER)
                        .pathMatchers("/api/core/contest/admin/**").hasRole(Role.ADMIN)
                        .pathMatchers("/api/core/contest/problem/**").hasRole(Role.USER)
                        .pathMatchers("/api/core/ranking/admin/**").hasRole(Role.ADMIN)
                        .pathMatchers(HttpMethod.GET, "/api/core/settings/**").permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/core/settings/**").hasRole(Role.ADMIN)
                        .pathMatchers("/api/core/file/data/**").hasRole(Role.ADMIN)
                        .pathMatchers(HttpMethod.GET, "/api/core/file/img/**").permitAll()
                        .pathMatchers(HttpMethod.DELETE, "/api/core/file/img/**").hasRole(Role.ADMIN)
                        .pathMatchers(HttpMethod.POST, "/api/core/file/img/problem/**").hasRole(Role.ADMIN)
                        .pathMatchers(HttpMethod.POST, "/api/core/file/img/avatar/**").hasRole(Role.USER)
                        .pathMatchers("/api/judge/submit").hasRole(Role.USER)
                        .pathMatchers("/api/judge/admin/**").hasRole(Role.ADMIN)
                        .anyExchange()
                        .permitAll()
                )
                .addFilterAt(loginFilter(serverCodecConfigurer), SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAt(tokenVerifyFilter(), SecurityWebFiltersOrder.AUTHORIZATION)
                .build();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        var authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userService);
        authenticationManager.setPasswordEncoder(new BCryptPasswordEncoder());
        return authenticationManager;
    }
}
