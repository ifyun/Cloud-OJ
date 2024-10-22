package cloud.oj.gateway.config;

import cloud.oj.gateway.filter.LoginFilter;
import cloud.oj.gateway.filter.TokenVerifyFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 配置
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenVerifyFilter tokenVerifyFilter;

    private static class Role {
        private static final String USER = "USER";
        private static final String ADMIN = "ADMIN";
    }

    @Bean
    public SecurityFilterChain securityWebFilterChain(HttpSecurity http) throws Exception {
        return http.requestCache(RequestCacheConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(registry -> registry
                        .requestMatchers("/core/**").denyAll()
                        .requestMatchers("/judge/**").denyAll()
                        .requestMatchers(HttpMethod.GET, "/api/core/user/profile").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/core/user/profile").hasRole(Role.USER)
                        .requestMatchers("/api/core/user/admin/**").hasRole(Role.ADMIN)
                        .requestMatchers("/api/core/problem/admin/**").hasRole(Role.ADMIN)
                        .requestMatchers("/api/core/solution/**").hasRole(Role.USER)
                        .requestMatchers("/api/core/solution/admin/**").hasRole(Role.ADMIN)
                        .requestMatchers("/api/core/contest/admin/**").hasRole(Role.ADMIN)
                        .requestMatchers("/api/core/contest/problem/**").hasRole(Role.USER)
                        .requestMatchers("/api/core/ranking/admin/**").hasRole(Role.ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/core/settings/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/core/settings/**").hasRole(Role.ADMIN)
                        .requestMatchers("/api/core/log").hasRole(Role.ADMIN)
                        .requestMatchers("/api/core/file/data/**").hasRole(Role.ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/core/file/img/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE, "/api/core/file/img/**").hasRole(Role.ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/core/file/img/problem/**").hasRole(Role.ADMIN)
                        .requestMatchers(HttpMethod.POST, "/api/core/file/img/avatar/**").hasRole(Role.USER)
                        .requestMatchers("/api/judge/submit").hasRole(Role.USER)
                        .requestMatchers("/api/judge/admin/**").hasRole(Role.ADMIN)
                        .anyRequest()
                        .permitAll()
                )
                .addFilterAfter(tokenVerifyFilter, LoginFilter.class)
                .build();
    }
}
