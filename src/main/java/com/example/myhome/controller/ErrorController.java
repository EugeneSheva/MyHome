package com.example.myhome.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/admin/400")
    public String showBadRequestPage() {return "error/400";}

    @GetMapping("/admin/403")
    public String showForbiddenPage() {
        return "error/403";
    }

    @GetMapping("/admin/500")
    public String showServerErrorPage() {return "error/500";}

}
