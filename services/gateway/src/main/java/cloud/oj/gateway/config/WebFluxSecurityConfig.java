package cloud.oj.gateway.config;

import cloud.oj.gateway.filter.LoginFilter;
import cloud.oj.gateway.filter.TokenVerifyFilter;
import cloud.oj.gateway.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    public WebFluxSecurityConfig(UserService userService, ObjectMapper mapper) {
        this.userService = userService;
        this.mapper = mapper;
    }

    private static class Role {
        private static final String USER = "USER";
        private static final String UA = "USER_ADMIN";
        private static final String PA = "PROBLEM_ADMIN";
        private static final String SU = "ADMIN";
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
        return http.headers().frameOptions().disable()
                .and()
                .requestCache()
                .requestCache(NoOpServerRequestCache.getInstance())
                .and()
                .csrf().disable()
                .formLogin().disable()
                .logout().disable()
                .httpBasic().authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
                .and()
                .authorizeExchange()
                .pathMatchers("/core-service/**").denyAll()
                .pathMatchers("/judge-service/**").denyAll()
                .pathMatchers("/file-server/**").denyAll()
                .pathMatchers("/api/core/backup").hasRole(Role.PA)
                .pathMatchers(HttpMethod.GET, "/api/core/user/profile").permitAll()
                .pathMatchers(HttpMethod.PUT, "/api/core/user/profile").hasRole(Role.USER)
                .pathMatchers("/api/core/user/admin/**").hasRole(Role.UA)
                .pathMatchers("/api/core/problem/admin/**").hasRole(Role.PA)
                .pathMatchers("/api/core/history/**").hasRole(Role.USER)
                .pathMatchers("/api/core/contest/admin/**").hasRole(Role.PA)
                .pathMatchers("/api/core/contest/problem/**").hasRole(Role.USER)
                .pathMatchers("/api/core/ranking/admin/**").hasRole(Role.PA)
                .pathMatchers(HttpMethod.GET, "/api/core/settings/**").permitAll()
                .pathMatchers(HttpMethod.POST, "/api/core/settings/**").hasRole(Role.SU)
                .pathMatchers("/api/judge/commit").hasRole(Role.USER)
                .pathMatchers("/api/judge/admin/**").hasRole(Role.PA)
                .pathMatchers("/api/file/test_data/**").hasRole(Role.PA)
                .pathMatchers(HttpMethod.GET, "/api/file/image/**").permitAll()
                .pathMatchers(HttpMethod.DELETE, "/api/file/image/**").hasRole(Role.SU)
                .pathMatchers(HttpMethod.POST, "/api/file/image/problem/**").hasRole(Role.PA)
                .pathMatchers(HttpMethod.POST, "/api/file/image/avatar/**").hasRole(Role.USER)
                .anyExchange().permitAll()
                .and()
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
