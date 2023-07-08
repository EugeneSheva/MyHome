package com.example.myhome.controller;

import com.example.myhome.dto.AdminDTO;
import com.example.myhome.mapper.AdminDTOMapper;
import com.example.myhome.model.Admin;
import com.example.myhome.model.PageRoleDisplay;
import com.example.myhome.repository.PageRoleDisplayRepository;
import com.example.myhome.service.AdminService;
import com.example.myhome.service.OwnerService;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ControllerAdvice
@Log
public class GlobalControllerAdvice {

    @Autowired
    private PageRoleDisplayRepository repository;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminDTOMapper mapper;

    @Autowired
    private ErrorController errorController;

    @InitBinder
    private void activateDirectFieldAccess(DataBinder dataBinder) {
        dataBinder.initDirectFieldAccess();
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleMissingRequestParameter(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(NumberFormatException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleNumberFormatException(NumberFormatException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    // ловит любой оставшийся эксепшн (?)
    @ExceptionHandler(Exception.class)
    public String redirect(Exception e, Model model) {
        log.severe("Captured exception of class: " + e.getClass().toString());
        log.severe(e.getMessage());
        e.printStackTrace();
        return errorController.showServerErrorPage(e.getMessage(), model);
    }

    @ModelAttribute
    public void addPagePermissions(Model model) {
        List<PageRoleDisplay> pageRoles = repository.findAll();
        model.addAttribute("pageRoles", pageRoles);
    }

    @ModelAttribute
    public void addLoggedInAdmin(Model model) {
        if(SecurityContextHolder.getContext().getAuthentication() == null) return;
        Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!(object instanceof Admin)) return;
        else {
            Admin admin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(admin == null) return;
            admin = adminService.findAdminByLogin(admin.getUsername());
            AdminDTO dto = new AdminDTO();
            dto.setId(admin.getId());
            dto.setFirst_name(admin.getFirst_name());
            dto.setLast_name(admin.getLast_name());
            model.addAttribute("auth_admin", dto);
            model.addAttribute("newUserCount", ownerService.getNotificationOwnerCountForAdmin(admin));
        }

    }

    @ModelAttribute
    public void addNotificationOwnerCount(Model model) {

    }

//    @ModelAttribute
//    public void showRequestLocale(Model model, HttpServletRequest request) {
//        log.info("LOCALE: " + request.getLocale());
//        log.info("COUNTRY: " + request.getLocale().getCountry());
//        log.info("LANGUAGE: " + request.getLocale().getLanguage());
//    }

}
