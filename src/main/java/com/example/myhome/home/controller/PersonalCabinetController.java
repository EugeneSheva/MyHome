package com.example.myhome.home.controller;

import com.example.myhome.home.configuration.security.CustomUserDetails;
import com.example.myhome.home.model.Owner;
import com.example.myhome.home.model.RepairRequest;
import com.example.myhome.home.service.OwnerService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/cabinet")
@Log
public class PersonalCabinetController {

    @Autowired
    private OwnerService ownerService;

    @GetMapping
    public String getStartPage(){
        return "cabinet/index";
    }

    @GetMapping("/invoices")
    public String getInvoicePage() {
        return "cabinet/invoices";
    }

    @GetMapping("/invoices/{id}")
    public String getInvoiceInfoPage(@PathVariable long id, Model model) {
        return "cabinet/invoice_card";
    }

    @GetMapping("/tariffs")
    public String getTariffsPage() {
        return "cabinet/tariffs";
    }

    @GetMapping("/messages")
    public String getMessagesPage() {
        return "cabinet/messages";
    }

    @GetMapping("/messages/{id}")
    public String getMessageContentPage(@PathVariable long id) {
        return "cabinet/message_card";
    }

    @GetMapping("/requests")
    public String getRequestPage() {
        return "cabinet/requests";
    }

    @GetMapping("/requests/create")
    public String getCreateRequestPage(Model model) {
        RepairRequest repairRequest = new RepairRequest();
        repairRequest.setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
        repairRequest.setTime(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm")));
        System.out.println(repairRequest);
        model.addAttribute("repairRequest", repairRequest);
        return "cabinet/request_card";
    }

    @GetMapping("/user/view")
    public String getUserProfilePage(Model model) {
        return "cabinet/user_profile";
    }

    @GetMapping("/user/edit")
    public String getEditUserProfilePage(Model model) {
        model.addAttribute("owner", new Owner());
        return "cabinet/user_edit";
    }

//    @ModelAttribute
//    public void addOwnerAttribute(@AuthenticationPrincipal CustomUserDetails details, HttpSession session, Model model) {
//        if(details == null) {
//            details = (CustomUserDetails) session.getAttribute("user");
//        }
//        log.info(details.toString());
//        Owner owner = ownerService.fromCustomUserDetailsToOwner(details);
//        log.info(owner.toString());
//        model.addAttribute("owner", owner);
//    }

    @ModelAttribute
    public void v() {
        log.info(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
    }


}
