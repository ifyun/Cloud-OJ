package group._204.oj.gateway.config;

import group._204.oj.gateway.dao.UserDao;
import group._204.oj.gateway.filter.JwtLoginFilter;
import group._204.oj.gateway.filter.JwtVerifyFilter;
import group._204.oj.gateway.filter.LogoffFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                // 用户管理权限设置
                .antMatchers("/api/manager/user/pro/**").hasAnyRole("USER_ADMIN", "ROOT")
                // 题目管理权限设置
                .antMatchers("/api/manager/problem/pro/**").hasAnyRole("PROBLEM_ADMIN", "ROOT")
                .antMatchers("/api/manager/result").hasAnyRole("USER", "USER_ADMIN", "PROBLEM_ADMIN", "ROOT")
                // 竞赛/作业管理权限设置
                .antMatchers("/api/manager/contest/pro/**").hasAnyRole("PROBLEM_ADMIN", "ROOT")
                .antMatchers("/api/manager/contest/lang/**").permitAll()
                // 文件上传权限设置
                .antMatchers("/api/file/**").hasAnyRole("PROBLEM_ADMIN", "ROOT")
                // 判题服务权限设置
                .antMatchers("/api/judge/**").hasAnyRole("USER", "USER_ADMIN", "PROBLEM_ADMIN", "ROOT")
                // WEB 页面的权限设置
                .antMatchers("/manager_user").hasAnyRole("USER_ADMIN", "ROOT")
                .antMatchers("/manager_problem", "/manager_contest").hasAnyRole("PROBLEM_ADMIN", "ROOT");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(NoOpPasswordEncoder.getInstance());
    }
}
