package com.example.myhome;

import com.example.myhome.service.*;
import com.example.myhome.service.impl.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MyHomeApplicationTests {

    @MockBean
    private AccountServiceImpl accountServiceImpl;
    @MockBean
    private AdminServiceImpl adminService;
    @MockBean
    private ApartmentServiceImpl apartmentService;
    @MockBean
    private BuildingService buildingService;
    @MockBean
    private InvoiceService invoiceService;
    @MockBean
    private MessageService messageService;
    @MockBean
    private MeterDataServiceImpl meterDataService;
    @MockBean
    private OwnerService ownerService;
    @MockBean
    private RepairRequestService repairRequestService;
    @MockBean
    private ServiceServiceImpl serviceService;
    @MockBean
    private TariffServiceImpl tariffService;

    @Test
    void contextLoads() {
        assertThat(accountServiceImpl).isNotNull();
        assertThat(apartmentService).isNotNull();
        assertThat(adminService).isNotNull();
        assertThat(buildingService).isNotNull();
        assertThat(invoiceService).isNotNull();
        assertThat(messageService).isNotNull();
        assertThat(meterDataService).isNotNull();
        assertThat(ownerService).isNotNull();
        assertThat(repairRequestService).isNotNull();
        assertThat(serviceService).isNotNull();
        assertThat(tariffService).isNotNull();
    }

}
