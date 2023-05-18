package com.example.myhome;

import com.example.myhome.model.Admin;

import com.example.myhome.repository.AdminRepository;
import com.example.myhome.repository.UserRoleRepository;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@SpringBootApplication(scanBasePackages = {"com.example.myhome", "com.example.myhome.util"})
@EnableJpaRepositories(basePackages = {"com.example.myhome.repository", "com.example.myhome.service.registration"})
@EnableEncryptableProperties
@Log4j2
public class MyHomeApplication {

    @Autowired private UserRoleRepository repository;
    @Autowired private AdminRepository adminRepository;

    public static void main(String[] args) {
        SpringApplication.run(MyHomeApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void b() throws IOException {

        List<Admin> list = adminRepository.findAll();
        list.forEach(admin -> {
            if(admin.getRole() == null) admin.setRole(repository.getReferenceById(22L));
        });
        adminRepository.saveAll(list);

//        repository.saveAll(List.of(
//                new UserRole(1L, "Директор"),
//                new UserRole(2L, "Администратор"),
//                new UserRole(3L, "Бухгалтер"),
//                new UserRole(4L, "Сантехник"),
//                new UserRole(5L, "Управляющий"),
//                new UserRole(6L, "Электрик")
//        ));
//        repository.findByName("Администратор").ifPresent(role -> {
//            role.getPermissions().add("cashbox.read");
//            role.getPermissions().add("cashbox.write");
//            role.getPermissions().add("invoices.read");
//            role.getPermissions().add("invoices.write");
//            role.getPermissions().add("accounts.read");
//            role.getPermissions().add("accounts.write");
//            role.getPermissions().add("meters.read");
//            role.getPermissions().add("meters.write");
//            role.getPermissions().add("apartments.read");
//            role.getPermissions().add("apartments.write");
//            role.getPermissions().add("buildings.read");
//            role.getPermissions().add("buildings.write");
//            role.getPermissions().add("owners.read");
//            role.getPermissions().add("owners.write");
//            role.getPermissions().add("messages.read");
//            role.getPermissions().add("messages.write");
//            role.getPermissions().add("requests.read");
//            role.getPermissions().add("requests.write");
//            role.getPermissions().add("roles.read");
//            role.getPermissions().add("roles.write");
//        });
        adminRepository.findById(0L).ifPresent(admin -> admin.setRole(repository.findByName("Директор").orElse(null)));
        adminRepository.findById(9L).ifPresent(admin -> admin.setRole(repository.findByName("Администратор").orElse(null)));
    }

}
