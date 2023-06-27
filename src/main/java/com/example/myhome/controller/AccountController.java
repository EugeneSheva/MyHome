package com.example.myhome.controller;

import com.example.myhome.controller.socket.WebsocketController;
import com.example.myhome.dto.ApartmentAccountDTO;
import com.example.myhome.mapper.AccountDTOMapper;
import com.example.myhome.model.ApartmentAccount;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.service.AccountService;
import com.example.myhome.service.BuildingService;
import com.example.myhome.service.CashBoxService;
import com.example.myhome.service.OwnerService;
import com.example.myhome.validator.AccountValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/accounts")
@RequiredArgsConstructor
@Log
public class AccountController {

    private final AccountService accountService;
    private final CashBoxService cashBoxService;
    private final BuildingService buildingService;
    private final OwnerService ownerService;

    private final AccountValidator validator;

    private final AccountDTOMapper mapper;

    private final WebsocketController websocketController;

    private final MessageSource messageSource;

    // Открыть страничку с таблицей всех счетов
    @GetMapping
    public String showAccountsPage(Model model,
                                   FilterForm filterForm){

        model.addAttribute("cashbox_balance", cashBoxService.calculateBalance());
        model.addAttribute("account_balance", accountService.getSumOfAccountBalances());
        model.addAttribute("account_debt", accountService.getSumOfAccountDebts());
        model.addAttribute("owners", ownerService.findAllDTO());
        model.addAttribute("buildings", buildingService.findAllDTO());

        if(filterForm.getBuilding() != null)
            model.addAttribute("sections", buildingService.findById(filterForm.getBuilding()).getSections());

        model.addAttribute("filter_form", filterForm);
        model.addAttribute("page", filterForm.getPage());

        return "admin_panel/accounts/accounts";
    }

    // Открыть страничку профиля лицевого счёта
    @GetMapping("/{id}")
    public String showAccountInfoPage(@PathVariable long id, Model model) {
        model.addAttribute("account", accountService.findAccountDTOById(id));
        return "admin_panel/accounts/account_profile";
    }

    // Открыть страничку создания лицевого счёта
    @GetMapping("/create")
    public String showCreateAccountPage(Model model) {
        ApartmentAccountDTO dto = new ApartmentAccountDTO();

        model.addAttribute("apartmentAccountDTO", dto);
        model.addAttribute("id", accountService.getMaxAccountId()+1);
        model.addAttribute("buildings", buildingService.findAll());

        return "admin_panel/accounts/account_card";
    }

    // Открыть страничку обновления лицевого счёта
    @GetMapping("/update/{id}")
    public String showUpdateAccountPage(@PathVariable long id, Model model) {

        model.addAttribute("apartmentAccountDTO", accountService.findAccountDTOById(id));
        model.addAttribute("id", id);
        model.addAttribute("buildings", buildingService.findAllDTO());

        return "admin_panel/accounts/account_card";
    }

    // Сохранить созданный лицевой счёт
    @PostMapping("/create")
    public String createAccount(@ModelAttribute ApartmentAccountDTO apartmentAccountDTO,
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        log.info(apartmentAccountDTO.toString());
        validator.validate(apartmentAccountDTO, bindingResult);

        if(bindingResult.hasErrors()) {
            log.info("Errors found");
            log.info(bindingResult.getAllErrors().toString());

            ApartmentAccountDTO dto = (ApartmentAccountDTO) bindingResult.getTarget();
            log.info("DTO " + dto.toString());

            if(dto.getBuilding() != null && dto.getBuilding().getId() != null)
                dto.setBuilding(buildingService.findBuildingDTObyId(dto.getBuilding().getId()));

            model.addAttribute("apartmentAccountDTO", dto);
            model.addAttribute("id", accountService.getMaxAccountId()+1);
            model.addAttribute("buildings", buildingService.findAllDTO());
            model.addAttribute("validation","failed");
            return "admin_panel/accounts/account_card";
        } else {
            ApartmentAccount account = accountService.saveAccount(apartmentAccountDTO);

            websocketController.sendAccountItem(account);

            return "redirect:/admin/accounts";
        }


    }

    // Сохранить обновленный счёт
    @PostMapping("/update/{id}")
    public String updateAccount(@PathVariable long id,
                                @ModelAttribute ApartmentAccountDTO apartmentAccountDTO,
                                BindingResult bindingResult,
                                Model model) {

        validator.validate(apartmentAccountDTO, bindingResult);
        if(bindingResult.hasErrors()) {
            log.info("Errors found");
            log.info(bindingResult.getAllErrors().toString());
            model.addAttribute("buildings", buildingService.findAllDTO());
            model.addAttribute("validation", "failed");
            log.info(buildingService.findAllDTO().toString());
            return "admin_panel/accounts/account_card";
        }
        ApartmentAccount account = accountService.saveAccount(apartmentAccountDTO);

        websocketController.sendAccountItem(account);

        return "redirect:/admin/accounts";
    }

    // Удалить лицевой счёт
    @GetMapping("/delete/{id}")
    public String deleteAccount(@PathVariable long id, RedirectAttributes redirectAttributes) {
        try {
            accountService.deleteAccountById(id);
        } catch (Exception e) {
            log.severe("Account deletion error");
            redirectAttributes.addFlashAttribute("fail", messageSource.getMessage("account.delete.error", null, LocaleContextHolder.getLocale()));
        }
        return "redirect:/admin/accounts";
    }

    // Получение информации про лицевой счёт по его ID
    @GetMapping("/get-account-info")
    public @ResponseBody ResponseEntity<ApartmentAccount> getAccountFromID(@RequestParam long account_id) {
        try {
            if(!accountService.existsById(account_id)) return ResponseEntity.status(404).build();
            else {
                ApartmentAccount account = accountService.findAccountById(account_id);
                return ResponseEntity.ok(account);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }


    // Получить лицевой счёт конкретной квартиры через её ID
    @GetMapping("/get-flat-account")
    public @ResponseBody Long getAccountNumberFromFlat(@RequestParam long flat_id) {
        return accountService.getAccountNumberFromFlat(flat_id).getId();
    }

    // Получение лицевых счетов через AJAX
    @GetMapping("/get-accounts")
    public @ResponseBody Page<ApartmentAccountDTO> getAccounts(@RequestParam Integer page,
                                                               @RequestParam Integer size,
                                                               @RequestParam String filters) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        FilterForm form = mapper.readValue(filters, FilterForm.class);
        return accountService.findAllAccountsByFiltersAndPage(form, PageRequest.of(page-1, size));
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("accountsPageActive", true);
    }

}
