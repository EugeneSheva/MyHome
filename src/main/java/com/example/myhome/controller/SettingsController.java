package com.example.myhome.controller;


import com.example.myhome.service.SettingsService;
import com.example.myhome.service.UserRoleService;
import com.example.myhome.model.IncomeExpenseItems;
import com.example.myhome.model.PageRoleDisplay;
import com.example.myhome.model.PageRoleForm;
import com.example.myhome.model.PaymentDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@Log
public class SettingsController {

    private final SettingsService settingsService;
    private final UserRoleService userRoleService;

    // Редирект на статистику
    @GetMapping("/admin")
    public String redirectToStatPage() {
        return "redirect:/admin/statistics";
    }

    // Открыть страничку платёжных реквизитов
    @GetMapping("/admin/payment-details")
    public String showPaymentDetailsPage(Model model) {
        model.addAttribute("paymentDetails", settingsService.getPaymentDetails());
        return "admin_panel/system_settings/settings_payment";
    }

    // Открыть страничку со всеми статьями приходов/расходов
    @GetMapping("/admin/income-expense")
    public String showTransactionsPage(@RequestParam(required = false) String sort, Model model) {
        List<IncomeExpenseItems> transactions = new ArrayList<>();
        if(sort != null) {
            if(sort.equalsIgnoreCase("exp")) {
                transactions = settingsService.getAllTransactionItems(Sort.by(Sort.Direction.ASC, "incomeExpenseType"));
                model.addAttribute("type", "inc");
            }
            else if(sort.equalsIgnoreCase("inc")) {
                transactions = settingsService.getAllTransactionItems(Sort.by(Sort.Direction.DESC, "incomeExpenseType"));
                model.addAttribute("type", "exp");
            }
        } else {
            transactions = settingsService.getAllTransactionItems();
            model.addAttribute("type", "exp");
        }
        model.addAttribute("transactions", transactions);
        return "admin_panel/system_settings/settings_inc_exp";
    }

    // Открыть страничку создания новой статьи приходов/расходов
    @GetMapping("/admin/income-expense/create")
    public String showCreateTransactionPage(Model model) {
        model.addAttribute("incomeExpenseItems", new IncomeExpenseItems());
        return "admin_panel/system_settings/transaction_card";
    }

    // Сохранение новой статьи приходов/расходов
    @PostMapping("/admin/income-expense/create")
    public String createTransaction(@Valid @ModelAttribute IncomeExpenseItems item, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            log.info("errors found");
            log.info(bindingResult.getObjectName());
            log.info(bindingResult.getAllErrors().toString());
            model.addAttribute("validation", "failed");
            return "admin_panel/system_settings/transaction_card";
        }
        settingsService.saveTransactionItem(item);
        return "redirect:/admin/income-expense";
    }

    // Открыть страничку обновления статьи приходов/расходов
    @GetMapping("/admin/income-expense/update/{id}")
    public String showUpdateTransactionPage(@PathVariable long id, Model model) {
        model.addAttribute("incomeExpenseItems", settingsService.getTransactionItem(id));
        return "admin_panel/system_settings/transaction_card";
    }

    // Сохранить обновленную статью
    @PostMapping("/admin/income-expense/update/{id}")
    public String updateTransaction(@Valid @ModelAttribute IncomeExpenseItems item, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            log.info("errors found");
            log.info(bindingResult.getObjectName());
            log.info(bindingResult.getAllErrors().toString());
            return "admin_panel/system_settings/transaction_card";
        }
        settingsService.saveTransactionItem(item);
        return "redirect:/admin/income-expense";
    }

    // Удалить статью приходов/расходов по ID
    @GetMapping("/admin/income-expense/delete/{id}")
    public String deleteTransaction(@PathVariable long id) {
        settingsService.deleteTransactionItem(id);
        return "redirect:/admin/income-expense";
    }

    // Сохранить платёжные реквизиты
    @PostMapping("/admin/payment-details")
    public String updatePaymentDetails(@Valid @ModelAttribute PaymentDetails details, BindingResult bindingResult,
                                       RedirectAttributes redirectAttributes, Model model) {
        if(bindingResult.hasErrors()) {
            log.info("errors found");
            log.info(bindingResult.getObjectName());
            log.info(bindingResult.getAllErrors().toString());
            model.addAttribute("validation", "failed");
            return "admin_panel/system_settings/settings_payment";
        }

        details.setId(1L);
        settingsService.savePaymentDetails(details);

        redirectAttributes.addFlashAttribute("success_message", "Сохранено!");
        return "redirect:/admin/payment-details";
    }

    // Открыть страничку разрешений для пользовательских ролей
    @GetMapping("/admin/roles")
    public String showRolesPage(Model model) {
        PageRoleForm pageForm = new PageRoleForm();
        pageForm.setPages(settingsService.getAllPagePermissions());
        model.addAttribute("pageForm", pageForm);
        return "admin_panel/system_settings/roles";
    }

    // Сохранить все разрешения для пользовательских ролей
    @PostMapping("/admin/roles")
    public String saveRolesPage(@ModelAttribute PageRoleForm pageForm, RedirectAttributes redirectAttributes, Model model) {
        List<PageRoleDisplay> originalList = settingsService.getAllPagePermissions();
        List<PageRoleDisplay> pages = pageForm.getPages();
        for(int i = 0; i < pages.size(); i++) {
            pages.get(i).setId((long) i+1);
            pages.get(i).setPage_name(originalList.get(i).getPage_name());
            pages.get(i).setRole_director(true);
            pages.get(i).setCode(originalList.get(i).getCode());
        }
        settingsService.savePagePermissions(pages);
        userRoleService.updateRoles(pages);
        redirectAttributes.addFlashAttribute("success", "Сохранено!");
        return "redirect:/admin/roles";
    }

}
