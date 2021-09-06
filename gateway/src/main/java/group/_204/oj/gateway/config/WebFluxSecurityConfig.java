package group._204.oj.gateway.config;

import group._204.oj.gateway.filter.LoginFilter;
import group._204.oj.gateway.filter.TokenVerifyFilter;
import group._204.oj.gateway.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import javax.annotation.Resource;

/**
 * Spring Security 配置
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebFluxSecurityConfig {

    @Resource
    private UserService userService;

    private static class Role {
        private static final String USER = "USER";
        private static final String UA = "USER_ADMIN";
        private static final String PA = "PROBLEM_ADMIN";
        private static final String SU = "ROOT";
    }

    @Bean
    public LoginFilter loginFilter(ServerCodecConfigurer serverCodecConfigurer) {
        return new LoginFilter(authenticationManager(), serverCodecConfigurer);
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
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable()
                .authorizeExchange()
                .pathMatchers("/core-service/**").denyAll()
                .pathMatchers("/judge-service/**").denyAll()
                .pathMatchers("/file-server/**").denyAll()
                .pathMatchers("/api/core/backup").hasRole(Role.PA)
                .pathMatchers(HttpMethod.GET, "/api/core/user/profile").permitAll()
                .pathMatchers(HttpMethod.PUT, "/api/core/user/profile").hasRole(Role.USER)
                .pathMatchers("/api/core/user/pro/**").hasRole(Role.UA)
                .pathMatchers("/api/core/problem/pro/**").hasRole(Role.PA)
                .pathMatchers("/api/core/history/**").hasRole(Role.USER)
                .pathMatchers("/api/core/contest/pro/**").hasRole(Role.PA)
                .pathMatchers("/api/core/contest/problem/**").hasRole(Role.USER)
                .pathMatchers("/api/core/ranking/pro/**").hasRole(Role.PA)
                .pathMatchers(HttpMethod.GET, "/api/core/settings/**").permitAll()
                .pathMatchers(HttpMethod.POST, "/api/core/settings/**").hasRole(Role.SU)
                .pathMatchers("/api/judge/commit").hasRole(Role.USER)
                .pathMatchers("/api/judge/pro/**").hasRole(Role.PA)
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
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager
                = new UserDetailsRepositoryReactiveAuthenticationManager(userService);
        authenticationManager.setPasswordEncoder(new BCryptPasswordEncoder());
        return authenticationManager;
    }
}
