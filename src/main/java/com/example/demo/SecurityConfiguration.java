package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.reactive.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    /*@Override
    public void configure(WebSecurity web) throws Exception{
        web.ignoring().antMatchers("/resources/**", "/css/**", "static/**").anyRequest();
    }*/

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .authorizeRequests()
                .antMatchers("/js/**", "/css/**","/register","/login",
                        "/styles/**", "/img/**", "/index", "/").permitAll()
                    .antMatchers("/admin").hasRole("ADMIN")
                    .antMatchers("/add-todo","/edit-todo","list-todos").hasAnyRole("ADMIN", "USER")
//                .antMatchers("/register").permitAll()
                .and()
            .formLogin()
                .loginPage("/login").permitAll()
//                .defaultSuccessUrl("/", true)
                .and()
            .logout()
//                .invalidateHttpSession(true)
//                .clearAuthentication(true)
                .logoutSuccessUrl("/login?logout=true").permitAll();

//            httpSecurity.authorizeRequests().antMatchers("/add-todo","/edit-todo","list-todos").authenticated().and().formLogin();

        httpSecurity.csrf().disable();
        //httpSecurity.csrf().ignoringAntMatchers("/jdbc:mysql://admin@tododb.cluster-ck5cshs9f6iu.us-east-1.rds.amazonaws.com:3306/tododb/**");
        httpSecurity.headers().frameOptions().sameOrigin();
    }

    @Autowired
    private DataSource dataSource;

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.jdbcAuthentication()
                .dataSource(dataSource)
                .usersByUsernameQuery("select username, password, enabled from "
                        + "users_db where username=?")
                .authoritiesByUsernameQuery("select username, role from roles "
                        + "where username=?");
}

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
