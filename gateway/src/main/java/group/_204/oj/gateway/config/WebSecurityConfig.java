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
                .antMatchers("/manager-service/**").denyAll()
                .antMatchers("/judge-service/**").denyAll()
                .antMatchers("/file-server/**").denyAll()
                .antMatchers("/api/manager/backup").hasRole(Role.PA)
                .antMatchers("/api/manager/user/profile").hasRole(Role.USER)
                .antMatchers("/api/manager/user/pro/**").hasRole(Role.UA)
                .antMatchers("/api/manager/problem/pro/**").hasRole(Role.PA)
                .antMatchers("/api/manager/result").hasRole(Role.USER)
                .antMatchers("/api/manager/contest/pro/**").hasRole(Role.PA)
                .antMatchers("/api/manager/contest/problem/**").hasRole(Role.USER)
                .antMatchers("/api/manager/ranking/pro/**").hasRole(Role.PA)
                .antMatchers(HttpMethod.GET, "/api/manager/settings/**").hasRole(Role.USER)
                .antMatchers(HttpMethod.POST, "/api/manager/settings/**").hasRole(Role.SU)
                .antMatchers("/api/judge/commit").hasRole(Role.USER)
                .antMatchers("/api/judge/pro/**").hasRole(Role.PA)
                .antMatchers("/api/file/test_data/**").hasRole(Role.PA)
                .antMatchers(HttpMethod.GET, "/api/file/image/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/file/image/**").hasRole(Role.USER);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }
}
