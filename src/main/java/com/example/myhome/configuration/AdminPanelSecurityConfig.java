package com.example.myhome.configuration;

import com.example.myhome.service.AdminService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@Order(-1)
@Log
public class AdminPanelSecurityConfig {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private AdminService adminDetailsService;

//    @Bean
//    public OAuth2AuthorizedClientManager authorizedClientManager(
//            ClientRegistrationRepository clientRegistrationRepository,
//            OAuth2AuthorizedClientRepository authorizedClientRepository) {
//
//        OAuth2AuthorizedClientProvider authorizedClientProvider = //
//                OAuth2AuthorizedClientProviderBuilder.builder() //
//                        .authorizationCode() //
//                        .refreshToken() //
//                        .clientCredentials() //
//                        .password() //
//                        .build();
//
//        DefaultOAuth2AuthorizedClientManager authorizedClientManager =
//                new DefaultOAuth2AuthorizedClientManager( //
//                        clientRegistrationRepository, //
//                        authorizedClientRepository);
//        authorizedClientManager //
//                .setAuthorizedClientProvider(authorizedClientProvider);
//
//        return authorizedClientManager;
//    }
//}

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
        return tokenRepository;
    }
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {

        log.info("setting up admin filter chain");

        http.headers().httpStrictTransportSecurity().includeSubDomains(true).maxAgeInSeconds(31536000);

        http.antMatcher("/**")
                        .authorizeRequests()
                .antMatchers("/home","/about","/services","/contacts","/error","/webjars/**").permitAll();


        http.antMatcher("/admin/**")
                .authorizeRequests()
                .antMatchers("/dist/**").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/error").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/reset-password/**", "/reset-password", "reset-password", "/**/reset-password").permitAll()
                .antMatchers("/forgot-password/**", "/forgot-password", "forgot-password", "/**/forgot-password").permitAll()
                .antMatchers("/**/statistics").hasAuthority("statistics.read")
                .antMatchers(HttpMethod.GET,"/**/cashbox/**").hasAuthority("cashbox.read")
                .antMatchers(HttpMethod.POST, "/**/cashbox/**").hasAuthority("cashbox.write")
                .antMatchers(HttpMethod.GET,"/**/invoices/**").hasAuthority("invoices.read")
                .antMatchers(HttpMethod.POST, "/**/invoices/**").hasAuthority("invoices.write")
                .antMatchers(HttpMethod.GET,"/**/accounts/**").hasAuthority("accounts.read")
                .antMatchers(HttpMethod.POST, "/**/accounts/**").hasAuthority("accounts.write")
                .antMatchers(HttpMethod.GET,"/**/apartments/**").hasAuthority("apartments.read")
                .antMatchers(HttpMethod.POST, "/**/apartments/**").hasAuthority("apartments.write")
                .antMatchers(HttpMethod.GET,"/**/owners/**").hasAuthority("owners.read")
                .antMatchers(HttpMethod.POST, "/**/owners/**").hasAuthority("owners.write")
                .antMatchers(HttpMethod.GET,"/**/buildings/**").hasAuthority("buildings.read")
                .antMatchers(HttpMethod.POST, "/**/buildings/**").hasAuthority("buildings.write")
                .antMatchers(HttpMethod.GET,"/**/messages/**").hasAuthority("messages.read")
                .antMatchers(HttpMethod.POST, "/**/messages/**").hasAuthority("messages.write")
                .antMatchers(HttpMethod.GET,"/**/meters/**").hasAuthority("meters.read")
                .antMatchers(HttpMethod.POST, "/**/meters/**").hasAuthority("meters.write")
                .antMatchers(HttpMethod.GET,"/**/requests/**").hasAuthority("requests.read")
                .antMatchers(HttpMethod.POST, "/**/requests/**").hasAuthority("requests.write")
                .antMatchers(HttpMethod.GET,"/**/roles").hasAuthority("roles.read")
                .antMatchers(HttpMethod.POST,"/**/roles").hasAuthority("roles.write")
                .antMatchers(HttpMethod.GET, "/**/website/**").hasAuthority("website_settings.read")
                .antMatchers(HttpMethod.POST, "/**/website/**").hasAuthority("website_settings.write")
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().accessDeniedHandler(accessDeniedHandler())
                .and()
//                .oauth2Login(o2 -> o2.loginPage("/oauth2_login").permitAll()
//                        .authorizationEndpoint()
//                        .baseUri("/oauth2/authorize-client").and()
//                        .defaultSuccessUrl("/admin")
//                        .failureUrl("/oauth2_login?error"))
                .formLogin()
                    .loginPage("/admin/site/login").permitAll()
                    .loginProcessingUrl("/admin/site/login")
                    .defaultSuccessUrl("/admin")
                    .failureUrl("/admin/site/login?error=true")
                    .passwordParameter("password")
                    .usernameParameter("username")
                .and()
                    .logout()
                        .logoutSuccessUrl("/admin/site/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                .and()
                .rememberMe()
                    .key("secretKey")
                    .tokenValiditySeconds(6000);

        return http.build();
    }

}
