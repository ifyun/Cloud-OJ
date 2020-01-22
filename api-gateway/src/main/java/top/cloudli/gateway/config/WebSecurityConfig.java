package top.cloudli.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import top.cloudli.gateway.filter.JwtLoginFilter;
import top.cloudli.gateway.filter.JwtVerifyFilter;
import top.cloudli.gateway.service.UserService;

import javax.annotation.Resource;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private UserService userService;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .addFilterBefore(new JwtLoginFilter("/login", authenticationManager()),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtVerifyFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/login", "/logout").permitAll()
                // 用户管理权限设置
                .antMatchers("/api/manager/user/pro/**").hasAnyRole("USER_ADMIN", "ROOT")
                // 题目管理权限设置
                .antMatchers("/api/manager/problem").permitAll()
                .antMatchers("/api/manager/problem/pro/**").hasAnyRole("PROBLEM_ADMIN", "ROOT")
                .antMatchers("/api/manager/result").hasAnyRole("USER", "USER_ADMIN", "PROBLEM_ADMIN", "ROOT")
                // 竞赛/作业管理权限设置
                .antMatchers("/api/manager/contest").permitAll()
                .antMatchers("/api/manager/contest/pro/**").hasAnyRole("PROBLEM_ADMIN", "ROOT")
                // 文件上传权限设置
                .antMatchers("/api/file/**").hasAnyRole("PROBLEM_ADMIN", "ROOT")
                // 判题服务权限设置
                .antMatchers("/api/judge/**").hasAnyRole("USER", "PROBLEM_ADMIN", "ROOT");
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(NoOpPasswordEncoder.getInstance());
    }
}
