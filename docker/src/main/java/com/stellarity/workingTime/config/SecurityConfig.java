package com.stellarity.workingTime.config;

import com.stellarity.workingTime.config.JWT.JwtAuthenticationEntryPoint;
import com.stellarity.workingTime.config.JWT.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

/*@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/web/**")
                    .authorizeRequests()
                    .antMatchers("/web/registration").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/web/login")
                    .defaultSuccessUrl("/web/")
                    .permitAll()
                    .and()
                    .formLogin()
                    .and()
                    .logout()
                    .logoutUrl("/web/logout")
                    .permitAll()
                    .and()
                    .csrf().disable();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
}*/

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {


    @Configuration
    @Order(1)
    public static class WebSecurityConfig extends WebSecurityConfigurerAdapter {


        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http.antMatcher("/web/**")
                    .authorizeRequests()
                    .antMatchers("/web/registration").permitAll()
                    .anyRequest().authenticated()
                    .and()
                    .formLogin()
                    .loginPage("/web/login")
                    .defaultSuccessUrl("/web/")
                    .permitAll()
                    .and()
                    .formLogin()
                    .and()
                    .logout()
                    .logoutUrl("/web/logout")
                    .permitAll()
                    .and()
                    .csrf().disable();
        }
		
		@Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }

    }

    @Configuration
    @Order(2)
    public static class WebSecurityConfigApi extends WebSecurityConfigurerAdapter {
        @Bean
        @Override
        public AuthenticationManager authenticationManagerBean() throws Exception {
            return super.authenticationManagerBean();
        }

        @Autowired
        private PasswordEncoder passwordEncoder;

        @Autowired
        private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

        @Autowired
        private UserDetailsService jwtUserDetailsService;

        @Autowired
        private JwtRequestFilter jwtRequestFilter;

        @Autowired
        public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
            auth.userDetailsService(jwtUserDetailsService).passwordEncoder(passwordEncoder);
        }

        /*@Bean
        public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }*/


        @Override
        protected void configure(HttpSecurity httpSecurity) throws Exception {
            httpSecurity.antMatcher("/api/**")
                    .csrf().disable()
                    .authorizeRequests().antMatchers("/authenticate", "api/user/add").permitAll()
                    .anyRequest().authenticated().and()
                    .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and().sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
            httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        }
    }
}
