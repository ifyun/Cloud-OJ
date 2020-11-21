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
        private static final String[] ALL = {"USER", "USER_ADMIN", "PROBLEM_ADMIN", "ROOT"};
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
                .antMatchers("/api/manager/backup").hasAnyRole(Role.SU, Role.PA)
                .antMatchers("/api/manager/user/profile").hasAnyRole(Role.ALL)
                .antMatchers("/api/manager/user/pro/**").hasAnyRole(Role.UA, Role.SU)
                .antMatchers("/api/manager/problem/pro/**").hasAnyRole(Role.PA, Role.SU)
                .antMatchers("/api/manager/result").hasAnyRole(Role.ALL)
                .antMatchers("/api/manager/contest/pro/**").hasAnyRole(Role.PA, Role.SU)
                .antMatchers("/api/manager/contest/problem/**").hasAnyRole(Role.ALL)
                .antMatchers("/api/manager/ranking/pro/**").hasAnyRole(Role.PA, Role.SU)
                .antMatchers(HttpMethod.GET, "/api/manager/settings/**").hasAnyRole(Role.ALL)
                .antMatchers(HttpMethod.POST, "/api/manager/settings/**").hasAnyRole(Role.SU)
                .antMatchers("/api/judge/commit").hasAnyRole(Role.ALL)
                .antMatchers("/api/judge/pro/**").hasAnyRole(Role.SU)
                .antMatchers("/api/file/test_data/**").hasAnyRole(Role.PA, Role.SU)
                .antMatchers(HttpMethod.GET, "/api/file/image/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/file/image/**").hasAnyRole(Role.ALL);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }
}
