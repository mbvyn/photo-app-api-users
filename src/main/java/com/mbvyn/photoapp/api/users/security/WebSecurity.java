package com.mbvyn.photoapp.api.users.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.mbvyn.photoapp.api.users.service.UsersService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@SuppressWarnings("deprecation")
@EnableGlobalMethodSecurity(prePostEnabled=true)
@Configuration
@EnableWebSecurity
public class WebSecurity {

	private Environment environment;
	private UsersService usersService;
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	public WebSecurity(Environment environment, UsersService usersService,
			BCryptPasswordEncoder bCryptPasswordEncoder) {
		this.environment = environment;
		this.usersService = usersService;
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
	}

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {

        // Configure AuthenticationManagerBuilder
        AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(usersService).passwordEncoder(bCryptPasswordEncoder);

        // Get AuthenticationManager
        AuthenticationManager authenticationManager = authenticationManagerBuilder.build();

        http.cors().and().csrf().disable().authorizeRequests()
        		.requestMatchers(HttpMethod.POST, "/users").permitAll()
        		.requestMatchers("/h2-console/**").permitAll()
        		.anyRequest().authenticated()
                .and()
                .addFilter(getAuthenticationFilter(authenticationManager)).authenticationManager(authenticationManager)
                .addFilter(new AuthorizationFilter(authenticationManager, environment))
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        http.headers().frameOptions().disable();

        return http.build();
    }

	protected AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager)
			throws Exception {
		final AuthenticationFilter filter = new AuthenticationFilter(usersService, environment, authenticationManager);
		filter.setFilterProcessesUrl(environment.getProperty("login.url.path"));
		return filter;
	}
}