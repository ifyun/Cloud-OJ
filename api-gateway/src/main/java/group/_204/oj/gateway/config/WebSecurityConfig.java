package group._204.oj.gateway.config;

import group._204.oj.gateway.dao.UserDao;
import group._204.oj.gateway.filter.JwtLoginFilter;
import group._204.oj.gateway.filter.JwtVerifyFilter;
import group._204.oj.gateway.filter.LogoffFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import group._204.oj.gateway.service.UserService;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${project.token-valid-time:3}")
    private int validTime;

    @Resource
    private UserService userService;

    @Resource
    private UserDao userDao;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    private static class ROLE {
        static String[] ALL = {"USER", "USER_ADMIN", "PROBLEM_ADMIN", "ROOT"};
        static String UA = "USER_ADMIN";
        static String PA = "PROBLEM_ADMIN";
        static String SU = "ROOT";
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.headers().frameOptions().disable();
        http.csrf().disable()
                .addFilterBefore(new JwtLoginFilter(validTime, "/api/login", authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtVerifyFilter(userDao), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new LogoffFilter(userDao), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/api/login", "/api/logoff").permitAll()
                .antMatchers("/api/manager/user/pro/**").hasAnyRole(ROLE.UA, ROLE.SU)
                .antMatchers("/api/manager/problem/pro/**").hasAnyRole(ROLE.PA, ROLE.SU)
                .antMatchers("/api/manager/result").hasAnyRole(ROLE.ALL)
                .antMatchers("/api/manager/contest/pro/**").hasAnyRole(ROLE.PA, ROLE.SU)
                .antMatchers("/api/manager/contest/lang/**").permitAll()
                .antMatchers("/api/file/test_data/**").hasAnyRole(ROLE.PA, ROLE.SU)
                .antMatchers(HttpMethod.GET, "/api/file/image/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/file/image/**").hasAnyRole(ROLE.ALL)
                .antMatchers("/api/judge/**").hasAnyRole(ROLE.ALL)
                .antMatchers("/manager_user").hasAnyRole(ROLE.UA, ROLE.SU)
                .antMatchers("/manager_problem", "/manager_contest").hasAnyRole(ROLE.PA, ROLE.SU);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(NoOpPasswordEncoder.getInstance());
    }
}
