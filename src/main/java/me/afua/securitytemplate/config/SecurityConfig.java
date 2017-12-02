package me.afua.securitytemplate.config;

import me.afua.securitytemplate.repositories.AppUserRepository;
import me.afua.securitytemplate.services.SSUDS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AppUserRepository userRepository;

    @Bean
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return new SSUDS(userRepository);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception
    {
        http.authorizeRequests()
                .anyRequest().authenticated()
                .antMatchers("/","/h2-console/**").permitAll()
                .antMatchers("/granteduser").access("hasAuthority('USER')")
                .antMatchers("/grantedadmin").access("hasAuthority('ADMIN')")

                .and()
                .formLogin()
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login").permitAll();
    }

    @Override
    public void configure (AuthenticationManagerBuilder auth) throws Exception
    {
//        auth.inMemoryAuthentication().withUser("user").password("password").authorities("USER")
//                .and().withUser("admin").password("administrator").authorities("ADMIN");
        auth.userDetailsService(userDetailsServiceBean());
    }
}
