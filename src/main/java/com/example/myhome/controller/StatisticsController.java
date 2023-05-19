package com.example.myhome.controller;

import com.example.myhome.model.RepairStatus;
import com.example.myhome.repository.*;
import com.example.myhome.service.*;
import com.example.myhome.service.impl.AccountServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/statistics")
@Log
public class StatisticsController {

    private final RepairRequestRepository repairRequestRepository;
    private final OwnerService ownerService;
    private final BuildingService buildingService;
    private final BuildingRepository buildingRepository;
    private final ApartmentService apartmentService;
    private final AccountService accountService;
    private final CashBoxRepository cashBoxRepository;
    private final CashBoxService cashBoxService;
    private final AccountRepository accountRepository;
    private final ApartmentRepository apartmentRepository;
    private final InvoiceService invoiceService;
    private final OwnerRepository ownerRepository;

    @GetMapping
    public String getOwners(Model model) {
        model.addAttribute("newRepairRequestQuant", repairRequestRepository.countRepairRequestsByStatus(RepairStatus.ACCEPTED));
        model.addAttribute("inworkRepairRequestQuant", repairRequestRepository.countRepairRequestsByStatus(RepairStatus.IN_WORK));
        model.addAttribute("ownersQuant", ownerService.getQuantity());
        model.addAttribute("buidingsQuant", buildingService.getQuantity());
        model.addAttribute("apartmentsQuant", apartmentService.getQuantity());
        model.addAttribute("accountsQuant", accountService.getQuantity());

        model.addAttribute("cashBoxSum", cashBoxRepository.sumAmount().orElse(0.0));
        model.addAttribute("accountBalance", accountRepository.getSumOfAccountBalances());
        model.addAttribute("sumDebt", accountRepository.getSumOfAccountDebts());

        model.addAttribute("diagramIncomeCashBox", cashBoxService.getListSumIncomeByMonth());
        model.addAttribute("diagramExpenceCashBox", cashBoxService.getListSumExpenceByMonth());
        model.addAttribute("diagramMonths", cashBoxService.getListOfMonthName());

        model.addAttribute("diagramAllInvoices", invoiceService.getListSumInvoicesByMonth());
        model.addAttribute("diagramPaidInvoices", invoiceService.getListSumPaidInvoicesByMonth());

        return "admin_panel/statistics/statistics";
    }
}
