package com.example.myhome.config;

import com.example.myhome.model.Admin;
import com.example.myhome.model.UserRole;
import com.example.myhome.service.AccountService;
import com.example.myhome.service.ApartmentService;
import com.example.myhome.service.impl.AccountServiceImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.Set;

@TestConfiguration
public class TestConfig {

    @MockBean private ApartmentService apartmentService;

    @Bean
    public UserDetails testUser() {
        Admin admin = new Admin();
        admin.setId(9L);
        admin.setEmail("test");
        admin.setPassword("password");
        admin.setActive(true);
        UserRole role = new UserRole();
        role.setId(1L);
        role.setPermissions(Set.of("accounts.read","accounts.write",
                                    "cashbox.read","cashbox.write",
                                    "owners.read","owners.write",
                                    "buildings.read","buildings.write",
                                    "invoices.read","invoices.write",
                                    "tariffs.read","tariffs.write",
                                    "apartments.read","apartments.write",
                                    "meters.read","meters.write",
                                    "messages.read","messages.write",
                                    "requests.read","requests.write",
                                    "roles.read","roles.write"));
        admin.setRole(role);
        return admin;
    }

    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        UserDetails userDetails = testUser();
        InMemoryUserDetailsManager userDetailsManager = new InMemoryUserDetailsManager();
        userDetailsManager.createUser(testUser());
        return userDetailsManager;
    }

//    @Bean
//    public AccountService accountService() {
//        return new AccountServiceImpl();
//    }


}
