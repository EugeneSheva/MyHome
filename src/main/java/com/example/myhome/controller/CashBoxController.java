package com.example.myhome.controller;
import com.example.myhome.dto.CashBoxDTO;
import com.example.myhome.controller.socket.WebsocketController;
import com.example.myhome.dto.AdminDTO;
import com.example.myhome.dto.ApartmentAccountDTO;
import com.example.myhome.dto.OwnerDTO;
import com.example.myhome.mapper.AccountDTOMapper;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.AccountRepository;
import com.example.myhome.repository.CashBoxRepository;
import com.example.myhome.repository.IncomeExpenseRepository;
import com.example.myhome.repository.OwnerRepository;
import com.example.myhome.service.AdminService;
import com.example.myhome.service.CashBoxService;
import com.example.myhome.service.OwnerService;
import com.example.myhome.service.impl.IncomeExpenseItemServiceImpl;
import com.example.myhome.validator.CashBoxtValidator;
import com.example.myhome.model.*;
import com.example.myhome.service.AccountService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/cashbox")
@Transactional
public class CashBoxController {

    private final CashBoxService cashBoxService;
    private final OwnerService ownerService;
    private final OwnerRepository ownerRepository;
    private final AdminService adminService;
    private final AccountService accountService;
    private final IncomeExpenseItemServiceImpl incomeExpenseItemService;
    private final IncomeExpenseRepository incomeExpenseRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountServiceImpl;
    private final CashBoxRepository cashBoxRepository;
    private final CashBoxtValidator cashBoxtValidator;

    private final WebsocketController websocketController;

    private final AccountDTOMapper accountDTOMapper;


    @GetMapping
    public String getCashBox(Model model, @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC, size = 10) Pageable pageable) {
        Page<CashBox> cashBoxList = cashBoxService.findAll(pageable);
        model.addAttribute("cashBoxList", cashBoxList);
        model.addAttribute("cashBoxSum", cashBoxRepository.sumAmount().orElse(0.0));
        model.addAttribute("accountBalance", accountServiceImpl.getSumOfAccountBalances());
        model.addAttribute("sumDebt", accountServiceImpl.getSumOfAccountDebts());
        model.addAttribute("filterForm", new FilterForm());
        int pageNumber = 0;
        int pageSize = 2;
        Sort sort = Sort.by("fieldName").ascending(); // Сортировка по полю fieldName в порядке возрастания
        Page<Owner>ownerDTOList=ownerService.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("id").ascending()));
        model.addAttribute("owners", ownerDTOList);

        List<IncomeExpenseItems>incomeExpenseItems=incomeExpenseItemService.findAll();
        model.addAttribute("incomeExpenseItems", incomeExpenseItems);


        return "admin_panel/cash_box/cashboxes";
    }

    @GetMapping("/show-incomes")
    public String showAccountIncomePage(@RequestParam Long account_id, Model model) {
        List<CashBox> cashBoxList;
        cashBoxList = accountService.findAccountById(account_id).getTransactions();

        // -- перегоняю List в Page для того , чтобы в html-страничке ничего не ломалось --
        Pageable pageable = PageRequest.of(0, 5);
        final int start = (int)pageable.getOffset();
        final int end = Math.min((start + pageable.getPageSize()), cashBoxList.size());
        final Page<CashBox> cashBoxPage = new PageImpl<>(cashBoxList.subList(start, end), pageable, cashBoxList.size());
        // -- перегоняю List в Page для того , чтобы в html-страничке ничего не ломалось --

        model.addAttribute("cashBoxList", cashBoxPage);
        model.addAttribute("cashBoxSum", cashBoxRepository.sumAmount().orElse(0.0));
        model.addAttribute("accountBalance", accountServiceImpl.getSumOfAccountBalances());
        model.addAttribute("sumDebt", accountServiceImpl.getSumOfAccountDebts());
        model.addAttribute("filterForm", new FilterForm());
        int pageNumber = 0;
        int pageSize = 2;
        Sort sort = Sort.by("fieldName").ascending(); // Сортировка по полю fieldName в порядке возрастания
        Page<Owner>ownerDTOList=ownerService.findAll(PageRequest.of(pageNumber, pageSize, Sort.by("id").ascending()));
        model.addAttribute("owners", ownerDTOList);

        List<IncomeExpenseItems>incomeExpenseItems=incomeExpenseItemService.findAll();
        model.addAttribute("incomeExpenseItems", incomeExpenseItems);

        return "admin_panel/cash_box/cashboxes";
    }


    @GetMapping("/{id}")
    public String getCashBox(@PathVariable("id") Long id, Model model) {
        CashBox cashBox = cashBoxService.findById(id);
        model.addAttribute("cashBoxItem", cashBox);
        return "admin_panel/cash_box/cashbox";
    }

    @GetMapping("/newIncome")
    public String createCashBoxIn(@RequestParam(required = false) Long account_id, Model model) {
        List<IncomeExpenseItems>incomeItemsList=incomeExpenseRepository.findAllByIncomeExpenseType(IncomeExpenseType.INCOME);
        model.addAttribute("incomeItemsList", incomeItemsList);

        List<AdminDTO>adminDTOList = adminService.findAllManagers();
        System.out.println(adminDTOList);
        // получение только юзеров с ролями "админ", "директор", "бухгалтер", без сантехников и т.д.
        model.addAttribute("admins", adminDTOList);

        System.out.println(adminDTOList);

        List<OwnerDTO> ownerDTOList = ownerService.findAllDTO();
        model.addAttribute("owners", ownerDTOList);

        List<ApartmentAccountDTO> apartmentAccountDTOS = accountService.findAllAccounts().stream().map(accountDTOMapper::fromAccountToDTO).collect(Collectors.toList());
        model.addAttribute("accounts", apartmentAccountDTOS);

        model.addAttribute("nextId", cashBoxService.getMaxId()+1);

        CashBox cashBox = new CashBox();

        if(account_id != null) {
            System.out.println("if block, id is not null");
            ApartmentAccount account = accountService.findAccountById(account_id);
            cashBox.setApartmentAccount(account);
            System.out.println(account.getApartment().getOwner().toString());
        }

        cashBox.setIncomeExpenseType(IncomeExpenseType.INCOME);
        model.addAttribute("cashBoxItem", cashBox);
        return "admin_panel/cash_box/cashbox_edit";
    }

    @GetMapping("/newExpense")
    public String createCashBoxEx(Model model) {
        List<IncomeExpenseItems>expenseItemsList=incomeExpenseRepository.findAllByIncomeExpenseType(IncomeExpenseType.EXPENSE);
        model.addAttribute("expenseItemsList", expenseItemsList);

        model.addAttribute("nextId", cashBoxService.getMaxId()+1);

        List<AdminDTO>adminDTOList = adminService.findAllManagers();

        model.addAttribute("admins", adminDTOList);

        CashBox cashBox = new CashBox();
        cashBox.setIncomeExpenseType(IncomeExpenseType.EXPENSE);
        model.addAttribute("cashBoxItem", cashBox);
        return "admin_panel/cash_box/cashbox_edit";
    }

    @GetMapping("/edit/{id}")
    public String editeCashBox(@PathVariable("id") Long id, Model model) {
        CashBox cashBox = cashBoxService.findById(id);
        if(cashBox.getIncomeExpenseType().equals(IncomeExpenseType.INCOME)) {

            List<IncomeExpenseItems> incomeItemsList = incomeExpenseRepository.findAllByIncomeExpenseType(IncomeExpenseType.INCOME);
            model.addAttribute("incomeItemsList", incomeItemsList);

            List<OwnerDTO> ownerDTOList = ownerService.findAllDTO();
            model.addAttribute("owners", ownerDTOList);

            List<ApartmentAccountDTO> apartmentAccountDTOS = accountService.findAllAccounts().stream().map(accountDTOMapper::fromAccountToDTO).collect(Collectors.toList());
            model.addAttribute("accounts", apartmentAccountDTOS);

        } else if (cashBox.getIncomeExpenseType().equals(IncomeExpenseType.EXPENSE)){
            List<IncomeExpenseItems>expenseItemsList=incomeExpenseRepository.findAllByIncomeExpenseType(IncomeExpenseType.EXPENSE);
            model.addAttribute("expenseItemsList", expenseItemsList);
        }

        model.addAttribute("nextId", cashBoxService.getMaxId()+1);

        List<AdminDTO>adminDTOList = adminService.findAllManagers();

        model.addAttribute("admins", adminDTOList);

        model.addAttribute("cashBoxItem", cashBox);

        return "admin_panel/cash_box/cashbox_edit";
    }

    @GetMapping("/copy/{id}")
    public String copyCashBox(@PathVariable("id") Long id, Model model) {
        CashBox cashBox = cashBoxService.findById(id);
        if(cashBox.getIncomeExpenseType().equals(IncomeExpenseType.INCOME)) {

            List<IncomeExpenseItems> incomeItemsList = incomeExpenseRepository.findAllByIncomeExpenseType(IncomeExpenseType.INCOME);
            model.addAttribute("incomeItemsList", incomeItemsList);

            List<OwnerDTO> ownerDTOList = ownerService.findAllDTO();
            model.addAttribute("owners", ownerDTOList);

            List<ApartmentAccountDTO> apartmentAccountDTOS = accountService.findAllAccounts().stream().map(accountDTOMapper::fromAccountToDTO).collect(Collectors.toList());
            model.addAttribute("accounts", apartmentAccountDTOS);

        } else if (cashBox.getIncomeExpenseType().equals(IncomeExpenseType.EXPENSE)){
            List<IncomeExpenseItems>expenseItemsList=incomeExpenseRepository.findAllByIncomeExpenseType(IncomeExpenseType.EXPENSE);
            model.addAttribute("expenseItemsList", expenseItemsList);
        }


        List<AdminDTO>adminDTOList = adminService.findAllManagers();

        model.addAttribute("admins", adminDTOList);

        cashBox.setId(null);
        model.addAttribute("cashBoxItem", cashBox);
        return "admin_panel/cash_box/cashbox_edit";
    }

    @PostMapping("/save")
    public String saveCashBox(@Valid @ModelAttribute("cashBoxItem") CashBox cashBoxItem, BindingResult bindingResult, @RequestParam(name = "id", defaultValue = "0") Long id, @RequestParam(name = "owner", defaultValue = "0") Long ownerId,
                              @RequestParam(name = "manager", defaultValue = "0") Long adminId, @RequestParam(name = "apartmentAccount", defaultValue = "0") Long accountId,
                              @RequestParam(name = "amount", defaultValue = "0D") Double amount, @RequestParam(name = "incomeExpenseItems", defaultValue = "0") Long incomeExpenseItemId, Model model) throws IOException {
        cashBoxtValidator.validate(cashBoxItem, bindingResult);

        System.out.println(cashBoxItem);
        System.out.println("bindingResult"+ bindingResult);
        if (bindingResult.hasErrors()) {
            if(cashBoxItem.getIncomeExpenseType().equals(IncomeExpenseType.INCOME)){
                List<IncomeExpenseItems>incomeItemsList=incomeExpenseRepository.findAllByIncomeExpenseType(IncomeExpenseType.INCOME);
                model.addAttribute("incomeItemsList", incomeItemsList);

                List<OwnerDTO> ownerDTOList = ownerService.findAllDTO();
                model.addAttribute("owners", ownerDTOList);

                List<ApartmentAccountDTO> apartmentAccountDTOS = accountService.findAllAccounts().stream().map(accountDTOMapper::fromAccountToDTO).collect(Collectors.toList());
                model.addAttribute("accounts", apartmentAccountDTOS);
            } else if (cashBoxItem.getIncomeExpenseType().equals(IncomeExpenseType.EXPENSE)){
                List<IncomeExpenseItems>expenseItemsList=incomeExpenseRepository.findAllByIncomeExpenseType(IncomeExpenseType.EXPENSE);
                model.addAttribute("expenseItemsList", expenseItemsList);
            }
            List<AdminDTO>adminDTOList = adminService.findAllDTO();
            model.addAttribute("admins", adminDTOList);

            return "admin_panel/cash_box/cashBoxItem_edit";
        } else {
            if (cashBoxItem.getIncomeExpenseType() == IncomeExpenseType.EXPENSE) {
                if (amount > 0) cashBoxItem.setAmount(amount * (-1));
            } else {
                cashBoxItem.setOwner(ownerService.findById(ownerId));
                ApartmentAccount account = accountService.findAccountById(accountId);
                if(!account.getTransactions().contains(cashBoxItem)) {
                    account.getTransactions().add(cashBoxItem);
                    account.addToBalance(cashBoxItem.getAmount());
                    cashBoxItem.setApartmentAccount(account);
                }
            }
            cashBoxItem.setIncomeExpenseItems(incomeExpenseItemService.findById(incomeExpenseItemId));
            cashBoxItem.setManager(adminService.findAdminById(adminId));
            cashBoxService.save(cashBoxItem);
            return "redirect:/admin/cashbox/";
        }
    }

    @PostMapping("/newIncome")
    public String saveNewIncome(@ModelAttribute CashBox cashBoxItem, BindingResult bindingResult, Model model) {
        cashBoxtValidator.validate(cashBoxItem, bindingResult);
        if(bindingResult.hasErrors()) {
            return "admin_panel/cash_box/cashbox_edit";
        }
        cashBoxService.save(cashBoxItem);

        websocketController.sendCashboxItem(cashBoxItem);

        return "redirect:/admin/cashbox";
    }

    @PostMapping("/newExpense")
    public String saveNewExpense(@ModelAttribute CashBox cashBoxItem, BindingResult bindingResult, Model model) {
        cashBoxItem.setAmount(cashBoxItem.getAmount()*-1);
        cashBoxtValidator.validate(cashBoxItem, bindingResult);
        if(bindingResult.hasErrors()) {
            return "admin_panel/cash_box/cashbox_edit";
        }
        cashBoxService.save(cashBoxItem);

        websocketController.sendCashboxItem(cashBoxItem);

        return "redirect:/admin/cashbox";
    }

    @PostMapping("/edit/{id}")
    public String editCashBox(@ModelAttribute CashBox cashBoxItem, BindingResult bindingResult, Model model) {
        cashBoxtValidator.validate(cashBoxItem, bindingResult);
        if(bindingResult.hasErrors()) {
            return "admin_panel/cash_box/cashbox_edit";
        }
        cashBoxService.save(cashBoxItem);

        websocketController.sendCashboxItem(cashBoxItem);

        return "redirect:/admin/cashbox";
    }

    @GetMapping("/delete/{id}")
    public String dellete(@PathVariable("id") Long id) {
        cashBoxService.deleteById(id);
        return "redirect:/admin/cashbox/";
    }

    @GetMapping("/getUsers")
    @ResponseBody
    public Page<OwnerDTO> getOwners(@RequestParam(name = "searchQuerie", defaultValue = "") String searchQuerie,
                                    @RequestParam(name = "page", defaultValue = "0") int page,
                                    @RequestParam(name = "size", defaultValue = "2") int size) {

        System.out.println("page" + page + "size" + size);
        System.out.println("searchQuerie " + searchQuerie);
        Pageable pageable = PageRequest.of(page, size);

        Page<OwnerDTO> ownerPage = ownerService.findByNameFragmentDTO(searchQuerie, pageable);

        return ownerPage;
    }

    @GetMapping("/get-cashbox-page")
    public @ResponseBody Page<CashBoxDTO> getCashbox(@RequestParam Integer page,
                                                     @RequestParam Integer size,
                                                     @RequestParam String filters) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        FilterForm form = mapper.readValue(filters, FilterForm.class);
        return cashBoxService.findAllBySpecification2(form, page, size);
    }

    @GetMapping("/get-cashbox-balance")
    public @ResponseBody String getCashboxBalance() {
        return String.format(Locale.ROOT, "%.2f", cashBoxService.calculateBalance());
    }

    @GetMapping("/get-account-balance")
    public @ResponseBody String getAccountBalance() {
        return String.format(Locale.ROOT, "%.2f", accountServiceImpl.getSumOfAccountBalances());
    }

    @GetMapping("/get-account-debts")
    public @ResponseBody String getAccountDebts() {
        return String.format(Locale.ROOT,   "%.2f", accountServiceImpl.getSumOfAccountDebts());
    }



}
