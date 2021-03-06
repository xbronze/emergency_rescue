package com.china.rescue.framework.security.config;

import com.china.rescue.framework.security.filter.JwtAuthenticationTokenFilter;
import com.china.rescue.framework.security.handler.AuthenticationEntryPointImpl;
import com.china.rescue.framework.security.jwt.JWTFilter;
import com.china.rescue.framework.security.handler.MyAuthenticationSuccessHandler;
import com.china.rescue.framework.security.handler.MyLogoutSuccessHandler;
import com.china.rescue.framework.security.jwt.JWTProvider;
import com.china.rescue.framework.security.service.CustUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MySecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustUserDetailsService custUserDetailsService;

    @Autowired
    private AuthenticationEntryPointImpl authenticationEntryPoint;

    @Autowired
    private MyAuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private MyLogoutSuccessHandler logoutSuccessHandler;

    @Autowired
    private JWTProvider jwtProvider;

    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    /**
     * 解决无法注入 AuthenticationManager 的问题
     */
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(custUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin().loginProcessingUrl("/login").successHandler(authenticationSuccessHandler)
                .and().csrf().disable() //取消csrf防护
                .logout().logoutUrl("/logout").logoutSuccessHandler(logoutSuccessHandler).and()
//                .addFilterBefore(new JWTFilter(jwtProvider), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).and() // 地址访问验证失败不跳转登录页面，直接提示异常
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()  // 设置不使用httpSession存放认证信息
                .authorizeRequests()  // 过滤请求
                .antMatchers("/getToken/**").anonymous()
                .anyRequest().authenticated().and()
                .headers().frameOptions().disable() // 除上面外的所有请求全部需要认证
                .and()
                .httpBasic();
    }

    @Bean
    public static NoOpPasswordEncoder passwordEncoder() {
        return (NoOpPasswordEncoder) NoOpPasswordEncoder.getInstance(); // 不使用加密算法加密密码
        // return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        // 添加不做权限的URL
        web.ignoring()
                .antMatchers("/swagger-resources/**")
                .antMatchers("/swagger-ui.html")
                .antMatchers("/webjars/**")
                .antMatchers("/v2/**")
                .antMatchers("/h2-console/**");
    }
}
