package com.example.myhome.controller;

import com.example.myhome.dto.ApartmentDTO;
import com.example.myhome.controller.socket.WebsocketController;
import com.example.myhome.dto.BuildingDTO;

import com.example.myhome.model.Admin;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.AdminRepository;
import com.example.myhome.repository.ApartmentRepository;
import com.example.myhome.service.ApartmentService;
import com.example.myhome.service.BuildingService;
import com.example.myhome.service.MessageService;
import com.example.myhome.service.OwnerService;
import com.example.myhome.validator.MessageValidator;
import com.example.myhome.model.Apartment;
import com.example.myhome.model.Message;
import com.example.myhome.model.Owner;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/messages")
public class MessageController {

    private final MessageService messageService;
    private final BuildingService buildingService;
    private final ApartmentRepository apartmentRepository;
    private final ApartmentService apartmentService;
    private final OwnerService ownerService;
    private final AdminRepository adminRepository;
    private final MessageValidator messageValidator;
    private final WebsocketController websocketController;

    @GetMapping
    public String getMessages(Model model, @PageableDefault(sort = {"id"}, direction = Sort.Direction.ASC, size = 10) Pageable pageable) {
        Page<Message> messagesList = messageService.findAll(pageable);
        model.addAttribute("messages", messagesList);
        model.addAttribute("totalPagesCount", messagesList.getTotalPages());
        model.addAttribute("filterForm", new FilterForm());
        return "admin_panel/messages/messages";
    }

    @GetMapping("/{id}")
    public String getMessage(@PathVariable("id") Long id, Model model) {
        Message message = messageService.findById(id);
        model.addAttribute("message", message);
        return "admin_panel/messages/message";
    }

    @GetMapping("/new")
    public String createMessage(Model model) {
        Message message = new Message();
        model.addAttribute("message", message);
        List<BuildingDTO> buildingList = buildingService.findAllDTO();
        model.addAttribute("buildings", buildingList);
        return "admin_panel/messages/message_edit";
    }



    @PostMapping("/save")
    public String saveMessage(@Valid @ModelAttribute("message") Message message, BindingResult bindingResult, @RequestParam(name = "debt",
                                defaultValue = "false") Boolean debt, @RequestParam(name = "building", defaultValue = "0") Long buildingId,
                              @RequestParam(name = "section", defaultValue = "") String section, @RequestParam(name = "floor", defaultValue = "") String floor,
                              @RequestParam(name = "apartmentId", defaultValue = "0") Long apartmentId,
                              @RequestParam(name = "recipient", defaultValue = "0") Long recipient,
                              Model model) throws IOException {
        messageValidator.validate(message, bindingResult);
        if (bindingResult.hasErrors()) {
            List<BuildingDTO> buildingList = buildingService.findAllDTO();
            model.addAttribute("buildings", buildingList);
            return "admin_panel/messages/message_edit";
        } else {
            List<Owner> recivers = new ArrayList<>();
            if (recipient == 0) {
                //geting recivers
                if (apartmentId == 0) {
                    if (floor.length() == 0 && section.length() == 0 && buildingId == 0 && debt == false) {
                        for (Apartment apartment : apartmentRepository.findAll()) {
                            if (!recivers.contains(apartment.getOwner())) recivers.add(apartment.getOwner());
                        }
                        message.setReceiversName("Всем");
                    } else if (floor.length() == 0 && section.length() == 0 && buildingId == 0 && debt == true) {
                        for (Apartment apartment : apartmentRepository.findApartmentsByBalanceBefore(0D)) {
                            if (!recivers.contains(apartment.getOwner())) recivers.add(apartment.getOwner());
                        }
                        message.setReceiversName("Всем с задоженностями");
                    } else if (floor.length() == 0 && section.length() == 0 && buildingId > 0 && debt == false) {
                        for (Apartment apartment : apartmentRepository.findApartmentsByBuildingId(buildingId)) {
                            if (!recivers.contains(apartment.getOwner())) recivers.add(apartment.getOwner());
                        }
                        message.setReceiversName(buildingService.findById(buildingId).getName());
                    } else if (floor.length() == 0 && section.length() == 0 && buildingId > 0 && debt == true) {
                        for (Apartment apartment : apartmentRepository.findApartmentsByBuildingIdAndBalanceBefore(buildingId, 0D)) {
                            if (!recivers.contains(apartment.getOwner())) recivers.add(apartment.getOwner());
                        }
                        message.setReceiversName(buildingService.findById(buildingId).getName() + " c задолженостями");
                    } else if (floor.length() == 0 && section.length() > 0 && buildingId > 0 && debt == false) {
                        for (Apartment apartment : apartmentRepository.findApartmentsByBuildingIdAndSectionContainingIgnoreCase(buildingId, section)) {
                            if (!recivers.contains(apartment.getOwner())) recivers.add(apartment.getOwner());
                        }
                        message.setReceiversName(buildingService.findById(buildingId).getName() + ", " + section);
                    } else if (floor.length() == 0 && section.length() > 0 && buildingId > 0 && debt == true) {
                        for (Apartment apartment : apartmentRepository.findApartmentsByBuildingIdAndSectionContainingIgnoreCaseAndBalanceBefore(buildingId, section, 0D)) {
                            if (!recivers.contains(apartment.getOwner())) recivers.add(apartment.getOwner());
                        }
                        message.setReceiversName(buildingService.findById(buildingId).getName() + ", " + section + " c задолженостями");
                    } else if (floor.length() > 0 && section.length() == 0 && buildingId > 0 && debt == false) {
                        for (Apartment apartment : apartmentRepository.findApartmentsByBuildingIdAndFloorContainingIgnoreCase(buildingId, floor)) {
                            if (!recivers.contains(apartment.getOwner())) recivers.add(apartment.getOwner());
                        }
                        message.setReceiversName(buildingService.findById(buildingId).getName() + ", " + floor);
                    } else if (floor.length() > 0 && section.length() == 0 && buildingId > 0 && debt == true) {
                        for (Apartment apartment : apartmentRepository.findApartmentsByBuildingIdAndFloorContainingIgnoreCaseAndBalanceBefore(buildingId, floor, 0D)) {
                            if (!recivers.contains(apartment.getOwner())) recivers.add(apartment.getOwner());
                        }
                        message.setReceiversName(buildingService.findById(buildingId).getName() + ", " + floor + " c задолженостями");
                    } else if (floor.length() > 0 && section.length() > 0 && buildingId > 0 && debt == false) {
                        for (Apartment apartment : apartmentRepository.findApartmentsByBuildingIdAndSectionContainingIgnoreCaseAndFloorContainingIgnoreCase(buildingId, section, floor)) {
                            if (!recivers.contains(apartment.getOwner())) recivers.add(apartment.getOwner());
                        }
                        message.setReceiversName(buildingService.findById(buildingId).getName() + ", " + section + ", " + floor);
                    } else if (floor.length() > 0 && section.length() > 0 && buildingId > 0 && debt == true) {
                        for (Apartment apartment : apartmentRepository.findApartmentsByBuildingIdAndSectionContainingIgnoreCaseAndFloorContainingIgnoreCaseAndBalanceBefore(buildingId, section, floor, 0D)) {
                            recivers.add(apartment.getOwner());
                        }
                        message.setReceiversName(buildingService.findById(buildingId).getName() + ", " + section + ", " + floor + " c задолженостями");
                    }
                } else if (apartmentId > 0) { //if we get one selected apartment
                    Apartment apartment = apartmentService.findById(apartmentId);
                    if (!recivers.contains(apartment.getOwner())) recivers.add(apartment.getOwner());
                    message.setReceiversName(apartment.getBuilding().getName() + ", " + apartment.getSection() + ", " + apartment.getFloor() + ", кв." + apartment.getNumber());
                }
            } else if (recipient > 0) {
                Owner owner = ownerService.findById(recipient);
                recivers.add(owner);
                message.setReceiversName(owner.getFullName());
            }
            Admin admin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            message.setSender(adminRepository.findByEmail(admin.getUsername()).orElseThrow());
            message.setReceivers(recivers);
            message.setUnreadReceivers(recivers);
            message.setDate(LocalDateTime.now());
            messageService.save(message);
            websocketController.sendMessagesItem(message);
            return "redirect:/admin/messages/";
        }
    }

    @GetMapping("/delete/{id}")
    public String dellete(@PathVariable("id") Long id) {
        messageService.deleteById(id);
        return "redirect:/admin/messages/";
    }

    @GetMapping("/getApartments")
    @ResponseBody
    public List<ApartmentDTO> getApartments(@RequestParam(name = "debt", defaultValue = "false") Boolean debt,
                                            @RequestParam(name = "building_id", defaultValue = "0") Long buildingId,
                                            @RequestParam(name = "section", defaultValue = "") String section,
                                            @RequestParam(name = "floor", defaultValue = "") String floor) {
        System.out.println("getApartments start");
        System.out.println("b=" + buildingId + " s=" + section.length() + " f=" + floor.length() + " d=" + debt);
        List<ApartmentDTO> apartmentDTOList = new ArrayList<>();

        if (floor.length() == 0 && section.length() == 0 && buildingId == 0 && debt == false) {
            apartmentDTOList = apartmentService.findDtoApartments();
        } else if (floor.length() == 0 && section.length() == 0 && buildingId == 0 && debt == true) {
            apartmentDTOList = apartmentService.findDtoApartmentsWithDebt();
        } else if (floor.length() == 0 && section.length() == 0 && buildingId > 0 && debt == false) {
            apartmentDTOList = apartmentService.findDtoApartmentsByBuilding(buildingId);
        } else if (floor.length() == 0 && section.length() == 0 && buildingId > 0 && debt == true) {
            apartmentDTOList = apartmentService.findDtoApartmentsByBuildingWithDebt(buildingId);
        } else if (floor.length() == 0 && section.length() > 0 && buildingId > 0 && debt == false) {
            apartmentDTOList = apartmentService.findDtoApartmentsByBuildingAndSection(buildingId, section);
        } else if (floor.length() == 0 && section.length() > 0 && buildingId > 0 && debt == true) {
            apartmentDTOList = apartmentService.findDtoApartmentsByBuildingAndSectionWithDebt(buildingId, section);
        } else if (floor.length() > 0 && section.length() == 0 && buildingId > 0 && debt == false) {
            apartmentDTOList = apartmentService.findDtoApartmentsByBuildingAndFloor(buildingId, floor);
        } else if (floor.length() > 0 && section.length() == 0 && buildingId > 0 && debt == true) {
            apartmentDTOList = apartmentService.findDtoApartmentsByBuildingAndFloorWithDebt(buildingId, floor);
        } else if (floor.length() > 0 && section.length() > 0 && buildingId > 0 && debt == false) {
            apartmentDTOList = apartmentService.findDtoApartmentsByBuildingAndSectionAndFloor(buildingId, section, floor);
        } else if (floor.length() > 0 && section.length() > 0 && buildingId > 0 && debt == true) {
            apartmentDTOList = apartmentService.findDtoApartmentsByBuildingAndSectionAndFloorWithDebt(buildingId, section, floor);
        }

        System.out.println("getApartments fin");
        System.out.println(apartmentDTOList);
        return apartmentDTOList;
    }

    @GetMapping("/deleteSelected")
    @ResponseBody
    public void deleteSelected(@RequestParam(name = "checkboxList") Long[] checkboxList) {
        System.out.println("deleteSelected start");
        for (Long aLong : checkboxList) {
            messageService.deleteById(aLong);
        }
    }

    @GetMapping("/get-messages")
    public @ResponseBody Page<Message> getOwners(@RequestParam Integer page,
                                                 @RequestParam Integer size,
                                                 @RequestParam String filters) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        FilterForm form = mapper.readValue(filters, FilterForm.class);
        System.out.println("controller " + form);
        return messageService.findAllBySpecification(form, page, size, null);
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("messagesPageActive", true);
    }
}

