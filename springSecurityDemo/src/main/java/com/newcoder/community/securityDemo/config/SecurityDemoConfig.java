package com.newcoder.community.securityDemo.config;

import com.newcoder.community.securityDemo.entity.User;
import com.newcoder.community.securityDemo.service.MyLogoutHandler;
import com.newcoder.community.securityDemo.service.UserService;
import com.newcoder.community.securityDemo.utils.CommunityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.Pbkdf2PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.InMemoryTokenRepositoryImpl;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
public class SecurityDemoConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    UserService userService;

    @Autowired
    MyLogoutHandler myLogoutHandler;

    //AuthenticationManager:认证的核心接口。
    //AuthenticationManagerBuilder是工具类，用于构建AuthenticationManager的工具。
    //ProviderManager:AuthenticationManager接口的默认实现类
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //内置的认证规则
        //auth.userDetailsService(userService).passwordEncoder(new Pbkdf2PasswordEncoder("12345"));

        //自定义认证规则
        //AuthenticationProvider: ProviderManager持有一组AuthenticationProvider，每个AuthenticationProvider负责一种认证
        //委托模式：ProviderManager将认证委托给AuthenticationProvider
        auth.authenticationProvider(new AuthenticationProvider() {
            //Authentication:用于封装认证信息的接口，不同的实现类代表不同类型的认证信息
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String username = authentication.getName();
                String password = (String) authentication.getCredentials();
                User user = userService.findUserByName(username);
                if(user == null)
                    throw new UsernameNotFoundException("账号不存在！");
                if(!user.getPassword().equals(CommunityUtil. md5(password+user.getSalt())))
                    throw new BadCredentialsException("密码错误！");

                return new UsernamePasswordAuthenticationToken(user,user.getPassword(),user.getAuthorities());
            }

            @Override
            public boolean supports(Class<?> authentication) {
                //这个是认证的方式，是否为账号密码认证。
                return UsernamePasswordAuthenticationToken.class.equals(authentication);
            }
        });

    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/static/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //super.configure(http);
        //登录相关的配置
        http.formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginPage("/loginPage")
                .loginProcessingUrl("/login")
                .successHandler(new AuthenticationSuccessHandler() {
                    @Override
                    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                        response.sendRedirect(request.getContextPath() +"/index");
                    }
                })
                .failureHandler(new AuthenticationFailureHandler() {
                    @Override
                    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                        request.setAttribute("error",exception.getMessage());
                        request.getRequestDispatcher("/loginPage").forward(request,response);
                    }
                });

        //登出
        // 退出相关配置
        http.logout()
                .logoutUrl("/logout")
                .addLogoutHandler(myLogoutHandler)
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                .permitAll();

        //授权配置
        http.authorizeRequests()
                .antMatchers("/privateMsg/**").hasAnyAuthority("USER","ADMIN")
                .antMatchers("/admin").hasAnyAuthority("ADMIN")
                .and().exceptionHandling().accessDeniedPage("/denied");

        //验证码处理
        http.addFilterBefore(new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                HttpServletRequest httpServletRequest= (HttpServletRequest) request;
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;

                if(httpServletRequest.getServletPath().equals("/login")){
                    String vcode = httpServletRequest.getParameter("verifyCode");
                    if(vcode== null || !vcode.equals("1234")){
                        request.setAttribute("error","验证码错误！");
                        request.getRequestDispatcher("/loginPage").forward(request,response);
                        return;
                    }
                }
                chain.doFilter(request,response);
            }
        }, UsernamePasswordAuthenticationFilter.class);


        //记住我：你要告诉它你用什么来记？用户信息记在内存，还是记在数据库里？
        http.rememberMe()
                .rememberMeParameter("remember-me")
                .tokenRepository(new InMemoryTokenRepositoryImpl()) //使用redis则自己实现一个接口。
                .tokenValiditySeconds(10* 60 * 60)
                .userDetailsService(userService); //

    }


}
