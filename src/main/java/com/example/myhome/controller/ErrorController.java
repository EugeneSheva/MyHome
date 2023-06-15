package com.example.myhome.controller;

import com.example.myhome.model.Admin;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;

@Controller
public class ErrorController {

    @GetMapping("/error")
    public String error(HttpServletRequest req) {
        String message = (String) req.getSession().getAttribute("error.message");
        System.out.println(LocalDateTime.now().toString() + " --- ERROR");
        System.out.println(message);
        return message;
    }

    @GetMapping("/admin/400")
    public String showBadRequestPage() {return "error/400";}

    @GetMapping("/admin/403")
    public String showForbiddenPage() {
        return "error/403";
    }

    @GetMapping("/admin/404")
    public String notFoundPage(HttpServletRequest req) {
        String message = (String) req.getSession().getAttribute("error.message");
        System.out.println(LocalDateTime.now().toString() + " --- ERROR");
        System.out.println(message);
        return "error/404";
    }

    @GetMapping("/admin/500")
    public String showServerErrorPage() {return "error/500";}

    @ModelAttribute
    public void addLoggedInAdmin(Model model) {
        {
            Admin admin = new Admin();
            admin.setFull_name("error");
            admin.setId(0L);
            model.addAttribute("auth_admin", admin);
        }
    }

}
