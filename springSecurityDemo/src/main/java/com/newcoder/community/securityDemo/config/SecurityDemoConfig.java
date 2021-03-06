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

    //AuthenticationManager:????????????????????????
    //AuthenticationManagerBuilder???????????????????????????AuthenticationManager????????????
    //ProviderManager:AuthenticationManager????????????????????????
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        //?????????????????????
        //auth.userDetailsService(userService).passwordEncoder(new Pbkdf2PasswordEncoder("12345"));

        //?????????????????????
        //AuthenticationProvider: ProviderManager????????????AuthenticationProvider?????????AuthenticationProvider??????????????????
        //???????????????ProviderManager??????????????????AuthenticationProvider
        auth.authenticationProvider(new AuthenticationProvider() {
            //Authentication:???????????????????????????????????????????????????????????????????????????????????????
            @Override
            public Authentication authenticate(Authentication authentication) throws AuthenticationException {
                String username = authentication.getName();
                String password = (String) authentication.getCredentials();
                User user = userService.findUserByName(username);
                if(user == null)
                    throw new UsernameNotFoundException("??????????????????");
                if(!user.getPassword().equals(CommunityUtil. md5(password+user.getSalt())))
                    throw new BadCredentialsException("???????????????");

                return new UsernamePasswordAuthenticationToken(user,user.getPassword(),user.getAuthorities());
            }

            @Override
            public boolean supports(Class<?> authentication) {
                //?????????????????????????????????????????????????????????
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
        //?????????????????????
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

        //??????
        // ??????????????????
        http.logout()
                .logoutUrl("/logout")
                .addLogoutHandler(myLogoutHandler)
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                .permitAll();

        //????????????
        http.authorizeRequests()
                .antMatchers("/privateMsg/**").hasAnyAuthority("USER","ADMIN")
                .antMatchers("/admin").hasAnyAuthority("ADMIN")
                .and().exceptionHandling().accessDeniedPage("/denied");

        //???????????????
        http.addFilterBefore(new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                HttpServletRequest httpServletRequest= (HttpServletRequest) request;
                HttpServletResponse httpServletResponse = (HttpServletResponse) response;

                if(httpServletRequest.getServletPath().equals("/login")){
                    String vcode = httpServletRequest.getParameter("verifyCode");
                    if(vcode== null || !vcode.equals("1234")){
                        request.setAttribute("error","??????????????????");
                        request.getRequestDispatcher("/loginPage").forward(request,response);
                        return;
                    }
                }
                chain.doFilter(request,response);
            }
        }, UsernamePasswordAuthenticationFilter.class);


        //??????????????????????????????????????????????????????????????????????????????????????????????????????
        http.rememberMe()
                .rememberMeParameter("remember-me")
                .tokenRepository(new InMemoryTokenRepositoryImpl()) //??????redis??????????????????????????????
                .tokenValiditySeconds(10* 60 * 60)
                .userDetailsService(userService); //

    }


}
