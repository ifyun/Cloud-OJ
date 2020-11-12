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

    private static class ROLE {
        static String[] ALL = {"USER", "USER_ADMIN", "PROBLEM_ADMIN", "ROOT"};
        static String UA = "USER_ADMIN";
        static String PA = "PROBLEM_ADMIN";
        static String SU = "ROOT";
    }

    @Resource
    private UserService userService;

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
                .antMatchers("/api/manager/backup").hasAnyRole(ROLE.SU, ROLE.PA)
                .antMatchers("/api/manager/user/profile").hasAnyRole(ROLE.ALL)
                .antMatchers("/api/manager/user/pro/**").hasAnyRole(ROLE.UA, ROLE.SU)
                .antMatchers("/api/manager/problem/pro/**").hasAnyRole(ROLE.PA, ROLE.SU)
                .antMatchers("/api/manager/result").hasAnyRole(ROLE.ALL)
                .antMatchers("/api/manager/contest/pro/**").hasAnyRole(ROLE.PA, ROLE.SU)
                .antMatchers("/api/manager/contest/lang/**").permitAll()
                .antMatchers("/api/manager/ranking/pro/**").hasAnyRole(ROLE.PA, ROLE.SU)
                .antMatchers(HttpMethod.GET, "/api/manager/settings/**").hasAnyRole(ROLE.ALL)
                .antMatchers(HttpMethod.POST, "/api/manager/settings/**").hasAnyRole(ROLE.SU)
                .antMatchers("/api/judge/commit").hasAnyRole(ROLE.ALL)
                .antMatchers("/api/judge/pro/**").hasAnyRole(ROLE.SU)
                .antMatchers("/api/file/test_data/**").hasAnyRole(ROLE.PA, ROLE.SU)
                .antMatchers(HttpMethod.GET, "/api/file/image/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/file/image/**").hasAnyRole(ROLE.ALL);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(new BCryptPasswordEncoder());
    }
}
