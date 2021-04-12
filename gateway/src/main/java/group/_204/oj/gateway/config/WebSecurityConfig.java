package group._204.oj.gateway.config;

import group._204.oj.gateway.filter.TokenVerifyFilter;
import group._204.oj.gateway.filter.LoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import group._204.oj.gateway.service.UserService;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private UserService userService;

    private static class Role {
        private static final String USER = "USER";
        private static final String UA = "USER_ADMIN";
        private static final String PA = "PROBLEM_ADMIN";
        private static final String SU = "ROOT";
    }

    @Bean
    public LoginFilter loginFilter() throws Exception {
        return new LoginFilter("/login", authenticationManager());
    }

    @Bean
    public TokenVerifyFilter tokenVerifyFilter() {
        return new TokenVerifyFilter();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();
        http.csrf().disable()
                .addFilterBefore(loginFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(tokenVerifyFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/core-service/**").denyAll()
                .antMatchers("/judge-service/**").denyAll()
                .antMatchers("/file-server/**").denyAll()
                .antMatchers("/api/core/backup").hasRole(Role.PA)
                .antMatchers(HttpMethod.GET, "/api/core/user/profile").permitAll()
                .antMatchers(HttpMethod.PUT, "/api/core/user/profile").hasRole(Role.USER)
                .antMatchers("/api/core/user/pro/**").hasRole(Role.UA)
                .antMatchers("/api/core/problem/pro/**").hasRole(Role.PA)
                .antMatchers("/api/core/result").hasRole(Role.USER)
                .antMatchers("/api/core/contest/pro/**").hasRole(Role.PA)
                .antMatchers("/api/core/contest/problem/**").hasRole(Role.USER)
                .antMatchers("/api/core/ranking/pro/**").hasRole(Role.PA)
                .antMatchers(HttpMethod.GET, "/api/core/settings/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/core/settings/**").hasRole(Role.SU)
                .antMatchers("/api/judge/commit").hasRole(Role.USER)
                .antMatchers("/api/judge/pro/**").hasRole(Role.PA)
                .antMatchers("/api/file/test_data/**").hasRole(Role.PA)
                .antMatchers(HttpMethod.GET, "/api/file/image/**").permitAll()
                .antMatchers(HttpMethod.DELETE, "/api/file/image/**").hasRole(Role.SU)
                .antMatchers(HttpMethod.POST, "/api/file/image/problem/**").hasRole(Role.PA)
                .antMatchers(HttpMethod.POST, "/api/file/image/avatar/**").hasRole(Role.USER);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }
}
