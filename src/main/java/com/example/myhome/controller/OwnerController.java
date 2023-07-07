package com.example.myhome.controller;

import com.example.myhome.dto.AdminDTO;
import com.example.myhome.dto.ApartmentDTO;
import com.example.myhome.dto.BuildingDTO;
import com.example.myhome.dto.InviteForm;
import com.example.myhome.dto.OwnerDTO;
import com.example.myhome.mapper.ApartmentDTOMapper;
import com.example.myhome.mapper.OwnerDTOMapper;
import com.example.myhome.model.*;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.OwnerRepository;
import com.example.myhome.service.*;
import com.example.myhome.util.UserStatus;
import com.example.myhome.validator.OwnerValidator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.LocaleContextResolver;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/owners")
@Log
public class OwnerController {

    @Value("${upload.path}")
    private String uploadPath;
    private final OwnerService ownerService;
    private final AdminService adminService;
    private final OwnerValidator ownerValidator;
    private final BuildingService buildingService;
    private final AccountService accountService;
    private final ApartmentService apartmentService;
    private final EmailService emailService;
    private final OwnerDTOMapper mapper;
    private final ApartmentDTOMapper apartmentDTOMapper;

    private final MessageSource messageSource;

    @GetMapping
    public String getOwners(Model model, @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC, size = 10) Pageable pageable) {
        List<BuildingDTO>buildingDTOList = buildingService.findAllDTO();
        model.addAttribute("buildings", buildingDTOList);
        model.addAttribute("filterForm", new FilterForm());
        return "admin_panel/owners/owners";
    }

    @GetMapping("/{id}")
    public String getOwner(@PathVariable("id") Long id, Model model) {
        OwnerDTO owner = ownerService.findByIdDTO(id);
        model.addAttribute("owner", owner);
        return "admin_panel/owners/owner";
    }

    @GetMapping("/new")
    public String createOwner(Model model) {
        OwnerDTO owner = new OwnerDTO();
        model.addAttribute("owner", owner);
        return "admin_panel/owners/owner_edit";
    }
    @GetMapping("edit/{id}")
    public String editeOwner(@PathVariable("id") Long id, Model model) {
        OwnerDTO owner = ownerService.findByIdDTO(id);
        model.addAttribute("owner", owner);
        return "admin_panel/owners/owner_edit";
    }

    @GetMapping("/invite")
    public String showInvitePage(Model model) {
        model.addAttribute("inviteForm", new InviteForm());
        return "admin_panel/owners/invite";
    }

    @PostMapping("/invite")
    public String inviteOwner(@RequestParam String phone,
                              @RequestParam String email,
                              Model model) {


        List<FieldError> errors = new ArrayList<>();
        if(phone.isEmpty()) errors.add(new FieldError("inviteForm", "phone", "Заповніть поле номер телефона"));
        else if(!phone.matches("\\+380[0-9]{9}")) errors.add(new FieldError("inviteForm", "phone", "Невірний формат номера телефона"));
        if(email.isEmpty()) errors.add(new FieldError("inviteForm", "email", "Заповніть E-mail"));
        else if(!ownerValidator.isValidEmailAddress(email)) errors.add(new FieldError("inviteForm", "email", "Невірний формат E-mail"));

        if(!errors.isEmpty()) {
            for (FieldError error : errors) {
                model.addAttribute(error.getField() + "Error", error.getDefaultMessage());
            }
            model.addAttribute("inviteForm", new InviteForm());
            model.addAttribute("phone", phone);
            model.addAttribute("email", email);
            return "admin_panel/owners/invite";
        } else {
            emailService.send(email, "Invite");
            model.addAttribute("invited", messageSource.getMessage("message.invite.sent", null, LocaleContextHolder.getLocale()));
            return "admin_panel/owners/invite";
        }
    }

    @PostMapping("/save")
    public String saveCoffee(@Valid @ModelAttribute("owner") OwnerDTO owner, BindingResult bindingResult, @RequestParam("img1") MultipartFile file, @RequestParam("newPassword") String newPassword, @RequestParam("repassword") String repassword) throws IOException {
        owner.setPassword(newPassword);
        owner.setConfirm_password(repassword);
        ownerValidator.validate(owner, bindingResult);
        if (bindingResult.hasErrors()) {
            System.out.println("bindingResult " + bindingResult);
            return "admin_panel/owners/owner_edit";
        } else if (!newPassword.equals(repassword) ){
            return "admin_panel/owners/owner_edit";
        } else {

            Owner newOwner = mapper.toEntityСabinetEditProfile(owner);

            if (owner.getId() != null) {
                Owner oldOwner = ownerService.findById(owner.getId());
                if (newPassword != null && newPassword.length() > 0 ) {
                    newOwner.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
                }else {
                    newOwner.setPassword(oldOwner.getPassword());
                }
            } else {
                newOwner.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
            }
            newOwner.setProfile_picture(ownerService.saveOwnerImage(owner.getId(), file));
            newOwner.setStatus(UserStatus.valueOf(owner.getStatus()));
            if (owner.getStatus().equalsIgnoreCase("DISABLED")) {
                newOwner.setEnabled(false);
            } else {
                newOwner.setEnabled(true);
            }

            ownerService.save(newOwner);
        }
        return "redirect:/admin/owners/";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Owner owner = ownerService.findById(id);
//        try {
//            Files.deleteIfExists(Path.of(uploadPath + owner.getProfile_picture()));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        try {
            ownerService.deleteById(id);
        } catch (Exception e) {
            log.warning("Exception caught");
            redirectAttributes.addFlashAttribute("delete_fail", messageSource.getMessage("owner.deletion.error", null, LocaleContextHolder.getLocale()));
        }
        return "redirect:/admin/owners/";
    }

    //Получить квартиры какого-то владельца
    @GetMapping("/get-apartments/{id}")
    public @ResponseBody List<ApartmentDTO> getOwnerApartments(@PathVariable long id) {
        return ownerService.findById(id).getApartments().stream().map(apartmentDTOMapper::fromApartmentToDTO).collect(Collectors.toList());
    }

    @GetMapping("/get-apartment-accounts")
    public @ResponseBody List<Long> getOwnerApartmentAccountsIds(@RequestParam long owner_id) {
        return ownerService.getOwnerApartmentAccountsIds(owner_id);
    }

    @GetMapping("/get-account-owner/{account_id}")
    public @ResponseBody OwnerDTO getSingleOwnerFromAccountId(@PathVariable long account_id) {
        ApartmentAccount account = accountService.findAccountById(account_id);
        Owner owner = account.getApartment().getOwner();
        return owner.transformIntoDTO();
    }

    @GetMapping("/get-apartment-owner/{apartment_id}")
    public @ResponseBody OwnerDTO getApartmentOwner(@PathVariable long apartment_id) {
        Apartment apartment = apartmentService.findById(apartment_id);
        return mapper.fromOwnerToDTO(apartment.getOwner());
    }

    @GetMapping("/get-owner")
    public @ResponseBody OwnerDTO getSingleOwner(@RequestParam long id) {
        return ownerService.findByIdDTO(id);
    }

    @GetMapping("/get-owners")
    public @ResponseBody Page<OwnerDTO> getOwners(@RequestParam Integer page,
                                                  @RequestParam Integer size,
                                                  @RequestParam String filters) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        FilterForm form = mapper.readValue(filters, FilterForm.class);
        System.out.println(form);
        return ownerService.findAllBySpecification2(form, page, size);
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

    @GetMapping("/get-all-owners")
    public @ResponseBody Map<String, Object> getAllOwners(@RequestParam String search, @RequestParam int page) {
        log.info(ownerService.findAllDTO().toString());
        log.info("Getting all owners that have in their name: " + search);
        Map<String, Object> map = new HashMap<>();
        Map<String, Boolean> pagination = new HashMap<>();
        pagination.put("more", (page*10L) < ownerService.countAllOwners());
        map.put("results", ownerService.getOwnerDTOByPage(search, page-1));
        map.put("pagination", pagination);
        System.out.println(map.get("results").toString());
        System.out.println(map.get("pagination").toString());
        System.out.println("map get-all-owners"+map);
        return map;
    }

    @GetMapping("/get-new-owners")
    public @ResponseBody List<OwnerDTO> getNewOwners() {
        if(SecurityContextHolder.getContext().getAuthentication() == null) return new ArrayList<>();
        Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!(object instanceof Admin)) return new ArrayList<>();
        else {
            Admin admin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(admin == null) return new ArrayList<>();
            admin = adminService.findAdminByLogin(admin.getUsername());
            System.out.println("found admin: " + admin.toString());
            List<OwnerDTO> list = ownerService.getNewOwnerDTOForAdmin(admin);
            System.out.println("Found list: " + list.toString());
            return list;
        }
    }

    @GetMapping("/newMessage")
    public String createMessage(Model model) {
        Message message = new Message();
        model.addAttribute("message", message);
        List<BuildingDTO> buildingList = buildingService.findAllDTO();
        model.addAttribute("buildings", buildingList);
        model.addAttribute("selectWithDebt", "selectWithDebt");
        return "admin_panel/messages/message_edit";
    }
    @GetMapping("/newTo/{id}")
    public String sendMessageToRecipient(@PathVariable("id") Long id, Model model) {
        Message message = new Message();
        model.addAttribute("message", message);
        OwnerDTO recipient = ownerService.findByIdDTO(id);
        model.addAttribute("recipient", recipient);
        List<BuildingDTO> buildingList = buildingService.findAllDTO();
        model.addAttribute("buildings", buildingList);
        return "admin_panel/messages/message_edit";
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("ownersPageActive", true);
    }
}
