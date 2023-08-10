package com.silversixx.bankingapp.security.config;

import com.silversixx.bankingapp.security.filters.JwtAuthorizationFilter;
import com.silversixx.bankingapp.security.filters.LoginProcessingFilter;
import com.silversixx.bankingapp.utils.JwtProperties;
import com.silversixx.bankingapp.entity.principal.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;
import static com.silversixx.bankingapp.security.authorities.Role.*;
import java.util.concurrent.TimeUnit;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsServiceImpl userDetailsServiceImpl;
    private final JwtProperties properties;
    private final PasswordEncoder passwordEncoder;
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
            .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(STATELESS)
            .and()
                .authorizeRequests()
                    .antMatchers("/api/v*/user/new","/api/v*/user/enable","/api/v*/user/refresh").permitAll()
                    .antMatchers("/api/v*/service/**").hasRole(USER.name())
                    .anyRequest().authenticated()
            .and()
                .addFilter(new LoginProcessingFilter(authenticationManagerBean(), properties))
                .addFilterBefore(new JwtAuthorizationFilter(properties), UsernamePasswordAuthenticationFilter.class)
                .formLogin()
            .and()
                .rememberMe()// enable rememberMe cookie
                .tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(30))
                .key("supersecret")
                .rememberMeParameter("remember-me")
            .and()
                .logout()
                    .logoutUrl("/logout")
                    .clearAuthentication(true)
                    .deleteCookies("remember-me")
                    .logoutSuccessUrl("/login")
        ;
    }
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsServiceImpl).passwordEncoder(passwordEncoder);
    }
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
//    @Bean
//    public void adminCredentialsBean() {
//        UserModel admin = UserModel.builder()
//                .email("admin")
//                .password(passwordEncoder.encode("admin"))
//                .fullName("admin")
//                .dob(null)
//                .gender(null)
//                .address(null)
//                .phoneNumber(null)
//                .accountNumber(null)
//                .accountBalance(null)
//                .enabled(true)
//                .accountNonExpired(true)
//                .accountNonLocked(true)
//                .credentialsNonExpired(true)
//                .roles(Set.of(ADMIN, USER))
//                .build();
//        userRepo.save(admin);
//    }
}
