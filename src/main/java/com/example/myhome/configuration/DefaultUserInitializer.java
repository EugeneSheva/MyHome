package com.example.myhome.configuration;
import com.example.myhome.model.Admin;
import com.example.myhome.model.UserRole;
import com.example.myhome.repository.AdminRepository;
import com.example.myhome.repository.UserRoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
class DefaultUserInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final UserRoleRepository userRoleRepository;

    public DefaultUserInitializer(AdminRepository adminRepository, UserRoleRepository userRoleRepository) {
        this.adminRepository = adminRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("run");
        System.out.println(adminRepository.findAll());
        System.out.println(adminRepository.findAll().size());

        if ((adminRepository.findAll() == null) || (adminRepository.findAll().size()==0)) {
            System.out.println("create");
            Admin admin = new Admin();
            admin.setFirst_name("default");
            admin.setLast_name("director");
            admin.setEmail("director");
            admin.setActive(true);
            admin.setPassword("$2a$12$FOhsYOephsRWkUHe2RoJcOZ/vAC0isIufmaNWB/rE4Lw07WZnBVZu");

            Set<String> permissions = new HashSet<>();
            permissions.add("statistics.read");
            permissions.add("cashbox.read");
            permissions.add("cashbox.write");
            permissions.add("invoices.read");
            permissions.add("invoices.write");
            permissions.add("accounts.read");
            permissions.add("accounts.write");
            permissions.add("apartments.read");
            permissions.add("apartments.write");
            permissions.add("owners.read");
            permissions.add("owners.write");
            permissions.add("buildings.read");
            permissions.add("buildings.write");
            permissions.add("messages.read");
            permissions.add("messages.write");
            permissions.add("meters.read");
            permissions.add("meters.write");
            permissions.add("requests.read");
            permissions.add("requests.write");
            permissions.add("roles.read");
            permissions.add("roles.write");
            permissions.add("services.read");
            permissions.add("services.write");
            permissions.add("tariffs.read");
            permissions.add("tariffs.write");
            permissions.add("users.read");
            permissions.add("users.write");
            permissions.add("payment_details.read");
            permissions.add("payment_details.write");
            permissions.add("transaction_items.read");
            permissions.add("transaction_items.write");
            permissions.add("website_settings.read");
            permissions.add("website_settings.write");
            UserRole userRole = new UserRole();
            userRole.setMaster(false);
            userRole.setName("Director");
            userRole.setPermissions(permissions);

            admin.setRole(userRole);
            adminRepository.save(admin);
        }
    }
}