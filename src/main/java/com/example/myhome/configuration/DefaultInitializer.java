package com.example.myhome.configuration;
import com.example.myhome.model.*;
import com.example.myhome.model.pages.AboutPage;
import com.example.myhome.model.pages.ContactsPage;
import com.example.myhome.model.pages.MainPage;
import com.example.myhome.model.pages.ServicesPage;
import com.example.myhome.repository.*;
import com.example.myhome.util.UserStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Component
@RequiredArgsConstructor
@Transactional
@Log
class DefaultInitializer implements CommandLineRunner {

    private final AdminRepository adminRepository;
    private final UserRoleRepository userRoleRepository;
    private final PageRepository pageRepository;
    private final DocumentRepository documentRepository;
    private final ServiceRepository serviceRepository;
    private final UnitRepository unitRepository;
    private final IncomeExpenseRepository incomeExpenseRepository;
    private final TariffRepository tariffRepository;
    private final PageRoleDisplayRepository pageRoleDisplayRepository;
    private final BuildingRepository buildingRepository;
    private final ApartmentRepository apartmentRepository;
    private final MeterDataRepository meterDataRepository;
    private final OwnerRepository ownerRepository;
    private final RepairRequestRepository repairRequestRepository;
    private final InvoiceRepository invoiceRepository;
    private final AccountRepository accountRepository;

    @Override
    public void run(String... args) throws Exception {
        checkForRoles();
        Thread.sleep(50);
        checkForAdmins();
        Thread.sleep(50);
        checkForServices();
        Thread.sleep(50);
        checkForTransactionItems();
        Thread.sleep(50);
        checkForTariffs();
        Thread.sleep(50);
        checkForOwners();
        Thread.sleep(50);
        checkForBuildings();
        Thread.sleep(50);
        checkForApartments();
        Thread.sleep(50);
        checkForAccounts();
        Thread.sleep(50);
        checkForPages();
        log.info("INITIAL CHECK COMPLETED");
    }

    void checkForBuildings() throws InterruptedException {
        if(buildingRepository.count() == 0 || buildingRepository.findAll().size() == 0) {
            log.info("NO BUILDINGS FOUND, ADDING");
            Building building = new Building();
            building.setName("TestBuilding");
            building.setAddress("TestBuildingAddress");
            building.setSections(List.of("TestBuildingSection1", "TestBuildingSection2"));
            building.setFloors(List.of("TestBuildingFloor1", "TestBuildingFloor2"));
            building.setAdmins(List.of(adminRepository.findById(1L).orElseThrow()));
            building.setApartments(new ArrayList<>());
            building.setImg1("1.jpg");
            building.setImg2("2.jpg");
            building.setImg3("3.jpg");
            building.setImg4("4.jpg");
            building.setImg5("5.jpg");
            buildingRepository.save(building);

        }
    }
    void checkForApartments() throws InterruptedException {
        if(apartmentRepository.count() == 0 || apartmentRepository.findAll().size() == 0) {
            log.info("NO APARTMENTS FOUND, ADDING");
            Apartment apartment = new Apartment();
            apartment.setNumber(1L);
            apartment.setBalance(100.0);
            apartment.setFloor("TestBuildingFloor1");
            apartment.setSection("TestBuildingSection1");
            apartment.setBuilding(buildingRepository.findById(1L).orElseThrow());
            apartment.setOwner(ownerRepository.findById(1L).orElseThrow());
            apartment.setTariff(tariffRepository.findById(1L).orElseThrow());
            apartment.setSquare(111.0);
            Apartment savedApartment = apartmentRepository.save(apartment);

            Building building = buildingRepository.findById(1L).orElseThrow();
            building.getApartments().add(savedApartment);

        }
    }
    void checkForOwners() throws InterruptedException {
//        if(ownerRepository.count() == 0 || ownerRepository.findAll().size() == 0) {
            log.info("NO OWNERS FOUND, ADDING");
            Owner owner = new Owner();
            owner.setFirst_name("Owner");
            owner.setFathers_name("For");
            owner.setLast_name("Testing");
            owner.setPhone_number("0501111111");
            owner.setDescription("description");
            owner.setEmail("user1@gmail.com");
            owner.setPassword("$2a$12$EnkCMqhGxG9LYts3mHCMvua/xno8CKNC2Ao.Ss3M4BYgXENSqUGWq");
            owner.setAdded_at(LocalDateTime.now());
            owner.setEnabled(true);
            owner.setStatus(UserStatus.ACTIVE);
            owner.setHas_debt(false);
            owner.setViber(owner.getPhone_number());
            owner.setTelegram(owner.getPhone_number());
            owner.setBirthdate(LocalDate.of(2000,11,11));

            ownerRepository.save(owner);

//        }
    }
    void checkForAccounts() throws InterruptedException {
        if(accountRepository.count() == 0 || accountRepository.findAll().size() == 0) {
            log.info("NO ACCOUNTS FOUND, ADDING");
            ApartmentAccount account = new ApartmentAccount();
            account.setIsActive(true);
            Apartment apartment = apartmentRepository.findById(1L).orElseThrow();
            account.setApartment(apartment);
            account.setSection(apartment.getSection());
            account.setBuilding(apartment.getBuilding());
            accountRepository.save(account);
        }
    }
    void checkForRoles() throws InterruptedException {
        if(userRoleRepository.count() == 0L || userRoleRepository.findAll().size() == 0){
            log.info("NO ROLES FOUND, ADDING");
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
            userRoleRepository.save(userRole);

            Set<String> permissions2 = new HashSet<>();
            permissions2.add("statistics.read");
            permissions2.add("cashbox.read");
            permissions2.add("cashbox.write");
            permissions2.add("invoices.read");
            permissions2.add("invoices.write");
            permissions2.add("accounts.read");
            permissions2.add("accounts.write");
            permissions2.add("apartments.read");
            permissions2.add("apartments.write");
            permissions2.add("owners.read");
            permissions2.add("owners.write");
            permissions2.add("buildings.read");
            permissions2.add("buildings.write");
            permissions2.add("messages.read");
            permissions2.add("messages.write");
            permissions2.add("meters.read");
            permissions2.add("meters.write");
            permissions2.add("requests.read");
            permissions2.add("requests.write");
            permissions2.add("roles.read");
            permissions2.add("roles.write");
            permissions2.add("services.read");
            permissions2.add("services.write");
            permissions2.add("tariffs.read");
            permissions2.add("tariffs.write");
            permissions2.add("payment_details.read");
            permissions2.add("payment_details.write");
            permissions2.add("transaction_items.read");
            permissions2.add("transaction_items.write");
            permissions2.add("website_settings.read");
            permissions2.add("website_settings.write");

            UserRole userRole2 = new UserRole();
            userRole2.setMaster(false);
            userRole2.setName("Manager");
            userRole2.setPermissions(permissions2);
            userRoleRepository.save(userRole2);

            Set<String> permissions3 = new HashSet<>();
            permissions3.add("cashbox.read");
            permissions3.add("cashbox.write");
            permissions3.add("invoices.read");
            permissions3.add("invoices.write");
            permissions3.add("accounts.read");
            permissions3.add("accounts.write");
            permissions3.add("services.read");
            permissions3.add("services.write");
            permissions3.add("tariffs.read");
            permissions3.add("tariffs.write");
            permissions3.add("payment_details.read");
            permissions3.add("payment_details.write");

            UserRole userRole3 = new UserRole();
            userRole3.setMaster(false);
            userRole3.setName("Accountant");
            userRole3.setPermissions(permissions3);
            userRoleRepository.save(userRole3);

            Set<String> permissions4 = new HashSet<>();
            permissions4.add("messages.read");
            permissions4.add("messages.write");
            permissions4.add("meters.read");
            permissions4.add("meters.write");
            permissions4.add("requests.read");
            permissions4.add("requests.write");

            UserRole userRole4 = new UserRole();
            userRole4.setMaster(true);
            userRole4.setName("Plumber");
            userRole4.setPermissions(permissions4);
            userRoleRepository.save(userRole4);

            Set<String> permissions5 = new HashSet<>();
            permissions5.add("messages.read");
            permissions5.add("messages.write");
            permissions5.add("meters.read");
            permissions5.add("meters.write");
            permissions5.add("requests.read");
            permissions5.add("requests.write");

            UserRole userRole5 = new UserRole();
            userRole5.setMaster(true);
            userRole5.setName("Electrician");
            userRole5.setPermissions(permissions5);
            userRoleRepository.save(userRole5);

            Set<String> permissions6 = new HashSet<>();
            permissions6.add("statistics.read");
            permissions6.add("cashbox.read");
            permissions6.add("cashbox.write");
            permissions6.add("invoices.read");
            permissions6.add("invoices.write");
            permissions6.add("accounts.read");
            permissions6.add("accounts.write");
            permissions6.add("apartments.read");
            permissions6.add("apartments.write");
            permissions6.add("owners.read");
            permissions6.add("owners.write");
            permissions6.add("buildings.read");
            permissions6.add("buildings.write");
            permissions6.add("messages.read");
            permissions6.add("messages.write");
            permissions6.add("meters.read");
            permissions6.add("meters.write");
            permissions6.add("requests.read");
            permissions6.add("requests.write");
            permissions6.add("roles.read");
            permissions6.add("roles.write");
            permissions6.add("services.read");
            permissions6.add("services.write");
            permissions6.add("tariffs.read");
            permissions6.add("tariffs.write");
            permissions6.add("users.read");
            permissions6.add("users.write");
            permissions6.add("payment_details.read");
            permissions6.add("payment_details.write");
            permissions6.add("transaction_items.read");
            permissions6.add("transaction_items.write");
            permissions6.add("website_settings.read");
            permissions6.add("website_settings.write");

            UserRole userRole6 = new UserRole();
            userRole6.setMaster(false);
            userRole6.setName("Admin");
            userRole6.setPermissions(permissions6);
            userRoleRepository.save(userRole6);


        }
        if(pageRoleDisplayRepository.count() == 0L || pageRoleDisplayRepository.findAll().size() == 0) {
            log.info("ADDING PAGE ROLES");
            PageRoleDisplay page1 = new PageRoleDisplay();
            page1.setCode("statistics");
            page1.setPage_name("Статистика");
            PageRoleDisplay page2 = new PageRoleDisplay();
            page2.setCode("cashbox");
            page2.setPage_name("Касса");
            PageRoleDisplay page3 = new PageRoleDisplay();
            page3.setCode("invoices");
            page3.setPage_name("Квитанции на оплату");
            PageRoleDisplay page4 = new PageRoleDisplay();
            page4.setCode("accounts");
            page4.setPage_name("Лицевые счета");
            PageRoleDisplay page5 = new PageRoleDisplay();
            page5.setCode("apartments");
            page5.setPage_name("Квартиры");
            PageRoleDisplay page6 = new PageRoleDisplay();
            page6.setCode("owners");
            page6.setPage_name("Владельцы квартир");
            PageRoleDisplay page7 = new PageRoleDisplay();
            page7.setCode("buildings");
            page7.setPage_name("Дома");
            PageRoleDisplay page8 = new PageRoleDisplay();
            page8.setCode("messages");
            page8.setPage_name("Сообщения");
            PageRoleDisplay page9 = new PageRoleDisplay();
            page9.setCode("requests");
            page9.setPage_name("Заявки вызова мастера");
            PageRoleDisplay page10 = new PageRoleDisplay();
            page10.setCode("meters");
            page10.setPage_name("Показания счётчика");
            PageRoleDisplay page11 = new PageRoleDisplay();
            page11.setCode("website_settings");
            page11.setPage_name("Управление сайтом");
            PageRoleDisplay page12 = new PageRoleDisplay();
            page12.setCode("services");
            page12.setPage_name("Услуги");
            PageRoleDisplay page13 = new PageRoleDisplay();
            page13.setCode("tariffs");
            page13.setPage_name("Тарифы");
            PageRoleDisplay page14 = new PageRoleDisplay();
            page14.setCode("roles");
            page14.setPage_name("Роли");
            PageRoleDisplay page15 = new PageRoleDisplay();
            page15.setCode("users");
            page15.setPage_name("Пользователи");
            PageRoleDisplay page16 = new PageRoleDisplay();
            page16.setCode("payment_details");
            page16.setPage_name("Платёжные реквизиты");
            PageRoleDisplay page17 = new PageRoleDisplay();
            page17.setCode("transaction_items");
            page17.setPage_name("Статьи платежей");
            pageRoleDisplayRepository.saveAll(List.of(page1,page2,page3,page4,page5,page6,page7,page8,page9,page10,
                    page11,page12,page13,page14,page15,page16,page17));
        }
    }
    void checkForTariffs() throws InterruptedException {
        if(tariffRepository.count() == 0L || tariffRepository.findAll().size() == 0) {
            log.info("NO TARIFFS FOUND, ADDING");
            Tariff tariff = new Tariff();
            tariff.setName("TestTariff");
            tariff.setDescription("TestDescription");
            tariff.setDate(LocalDateTime.now());
            tariff.setComponents(Map.of(serviceRepository.findById(1L).orElseThrow(), 10.0, serviceRepository.findById(2L).orElseThrow(), 200.5));
            tariffRepository.save(tariff);

        }
    }
    void checkForTransactionItems() throws InterruptedException {
        if(incomeExpenseRepository.count() == 0L || incomeExpenseRepository.findAll().size() == 0) {
            log.info("NO TRANSACTIONS FOUND, ADDING");
            IncomeExpenseItems testItem1 = new IncomeExpenseItems();
            testItem1.setIncomeExpenseType(IncomeExpenseType.INCOME);
            testItem1.setName("TestIncomeItem");
            IncomeExpenseItems testItem2 = new IncomeExpenseItems();
            testItem2.setIncomeExpenseType(IncomeExpenseType.EXPENSE);
            testItem2.setName("TestExpenseItem");

            incomeExpenseRepository.saveAll(List.of(testItem1, testItem2));

        }
    }
    void checkForServices() throws InterruptedException {
        if(serviceRepository.count() == 0L || serviceRepository.findAll().size() == 0) {
            log.info("NO SERVICES FOUND, ADDING");
            Service service = new Service();
            service.setName("TestService1");
            service.setShow_in_meters(true);
            Unit unit = new Unit();
            unit.setId(1L);
            unit.setName("TestUnit1");
            service.setUnit(unit);

            Service service2 = new Service();
            service2.setName("TestService2");
            service2.setShow_in_meters(true);
            Unit unit2 = new Unit();
            unit.setId(2L);
            unit2.setName("TestUnit2");
            service2.setUnit(unit2);

            unitRepository.saveAll(List.of(unit,unit2));
            serviceRepository.saveAll(List.of(service,service2));

        }
    }
    void checkForAdmins() throws InterruptedException {
        if ((adminRepository.findAll() == null) || (adminRepository.findAll().size()==0)) {
            log.info("NO ADMINS FOUND, ADDING");
            Admin admin = new Admin();
            admin.setFirst_name("default");
            admin.setLast_name("director");
            admin.setFull_name(admin.getFirst_name() + " " + admin.getLast_name());
            admin.setEmail("director");
            admin.setPhone_number("380997524927");
            admin.setActive(true);
            admin.setPassword("$2a$12$FOhsYOephsRWkUHe2RoJcOZ/vAC0isIufmaNWB/rE4Lw07WZnBVZu");
            admin.setRole(userRoleRepository.findByName("Director").orElseThrow());
            adminRepository.save(admin);

            Admin admin2 = new Admin();
            admin2.setFirst_name("default");
            admin2.setLast_name("manager");
            admin2.setEmail("manager");
            admin2.setActive(true);
            admin2.setPassword("$2a$12$y9N2z0X.U1zq.tbJuIuuXOL3fYWXwdx/ayJ5slGY86rKHk440da6e");
            admin2.setRole(userRoleRepository.findByName("Manager").orElseThrow());
            adminRepository.save(admin2);

            Admin admin3 = new Admin();
            admin3.setFirst_name("default");
            admin3.setLast_name("accountant");
            admin3.setEmail("accountant");
            admin3.setActive(true);
            admin3.setPassword("$2a$12$9iKcd6Iv1zOfrvprVJ8cOu3Wz3xNwRyxWvz7rpwUm2MxVKkDo28fS");
            admin3.setRole(userRoleRepository.findByName("Accountant").orElseThrow());
            adminRepository.save(admin3);

            Admin admin4 = new Admin();
            admin4.setFirst_name("default");
            admin4.setLast_name("plumber");
            admin4.setEmail("plumber");
            admin4.setActive(true);
            admin4.setPassword("$2a$12$jYq9lnyH.6sUe672K4rRd.Bu.oqCKt1lIa83jqi0RpWDxz2Vh0sxe");
            admin4.setRole(userRoleRepository.findByName("Plumber").orElseThrow());
            adminRepository.save(admin4);

            Admin admin5 = new Admin();
            admin5.setFirst_name("default");
            admin5.setLast_name("electrician");
            admin5.setEmail("electrician");
            admin5.setActive(true);
            admin5.setPassword("$2a$12$2V2z3.E.iRqdgV5TJP1Myesh6kXTT6I8m79LwPCsw/A4v04i3Tb7C");
            admin5.setRole(userRoleRepository.findByName("Electrician").orElseThrow());
            adminRepository.save(admin5);

            Admin admin6 = new Admin();
            admin6.setFirst_name("default");
            admin6.setLast_name("admin");
            admin6.setEmail("admin");
            admin6.setActive(true);
            admin6.setPassword("$2a$12$rKndiQg0lmkkwd2kcszrUunS/46DfX1eI4MxYi1.CxJlf57tQMvgK");
            admin6.setRole(userRoleRepository.findByName("Admin").orElseThrow());
            adminRepository.save(admin6);


        }
    }
    void checkForPages() throws InterruptedException {
        log.info("CHECKING FOR PAGES");
        if(pageRepository.getMainPage().isEmpty()) {
            MainPage page = new MainPage();
            page.setId(1L);
            page.setTitle("test");
            page.setDescription("test");
            page.setShow_links(true);
            page.setSlide1("7.jpg");
            page.setSlide2("8.jpg");
            page.setSlide3("9.jpg");
            page.setBlock_1_img("1.jpg");
            page.setBlock_2_img("2.jpg");
            page.setBlock_3_img("3.jpg");
            page.setBlock_4_img("4.jpg");
            page.setBlock_5_img("5.jpg");
            page.setBlock_6_img("6.jpg");
            page.setBlock_1_title("test");
            page.setBlock_2_title("test");
            page.setBlock_3_title("test");
            page.setBlock_4_title("test");
            page.setBlock_5_title("test");
            page.setBlock_6_title("test");
            page.setBlock_1_description("test");
            page.setBlock_2_description("test");
            page.setBlock_3_description("test");
            page.setBlock_4_description("test");
            page.setBlock_5_description("test");
            page.setBlock_6_description("test");
            pageRepository.save(page);
        }
        if(pageRepository.getAboutPage().isEmpty()) {
            AboutPage page = new AboutPage();
            page.setId(1L);
            page.setTitle("test");
            page.setDescription("test");
            page.setAdd_title("test");
            page.setAdd_description("test");
            page.setDirector_photo("7.jpg");
            page.setAdd_photos("4.jpg");
            page.setPhotos("1.jpg");
            AboutPage savedPage = pageRepository.save(page);
            List<AboutPage.Document> documents = new ArrayList<>();
            AboutPage.Document document = new AboutPage.Document();
            document.setName("test");
            document.setFile("6.jpg");
            document.setPage(savedPage);
            documentRepository.save(document);
            savedPage.setDocuments(documents);
            pageRepository.save(savedPage);
        }
        if(pageRepository.getContactsPage().isEmpty()) {
            ContactsPage page = new ContactsPage();
            page.setId(1L);
            page.setTitle("TestTitle");
            page.setDescription("TestDescription");
            page.setWebsite_link("http://www.google.com");
            page.setName("TestName");
            page.setLocation("TestLocation");
            page.setAddress("TestAddress");
            page.setPhone("+380997524927");
            page.setEmail("test@gmail.com");
            page.setMap_code("<iframe src=\"https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d2749.8910790498953!2d30.714207171687505!3d46.43103842231644!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x40c633a7b20ed43b%3A0xfaab4e45d64743a!2z0YPQuy4g0JrQvtGB0LzQvtC90LDQstGC0L7QsiwgMzIsINCe0LTQtdGB0YHQsCwg0J7QtNC10YHRgdC60LDRjyDQvtCx0LvQsNGB0YLRjCwgNjUwMDA!5e0!3m2!1sru!2sua!4v1679612885906!5m2!1sru!2sua\" width=\"100%\" height=\"600\" style=\"border:0;\" allowfullscreen=\"\" loading=\"lazy\" referrerpolicy=\"no-referrer-when-downgrade\"></iframe>");
            pageRepository.save(page);
        }
        if(pageRepository.getServicesPage().isEmpty()) {
            ServicesPage page = new ServicesPage();
            page.setId(1L);
            ServicesPage.ServiceDescription service = new ServicesPage.ServiceDescription();
            service.setTitle("test");
            service.setDescription("test");
            service.setPhoto("4.jpg");
            ServicesPage.ServiceDescription service2 = new ServicesPage.ServiceDescription();
            service2.setTitle("test");
            service2.setDescription("test");
            service2.setPhoto("6.jpg");
            ServicesPage.ServiceDescription service3 = new ServicesPage.ServiceDescription();
            service3.setTitle("test");
            service3.setDescription("test");
            service3.setPhoto("test1.jpg");
            List<ServicesPage.ServiceDescription> list = new ArrayList<>();
            list.add(service);
            page.setServiceDescriptions(list);
            pageRepository.save(page);
        }

    }
}