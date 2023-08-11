package com.example.myhome.controller;
import com.example.myhome.dto.AdminDTO;
import com.example.myhome.model.Admin;
import com.example.myhome.model.ForgotPasswordToken;
import com.example.myhome.repository.AdminRepository;
import com.example.myhome.repository.ForgotPasswordTokenRepository;
import com.example.myhome.service.EmailService;
import com.example.myhome.service.registration.LoginRequest;
import com.example.myhome.service.registration.RegistrationRequest;
import com.example.myhome.service.registration.RegisterService;
import com.example.myhome.validator.LoginRequestValidator;
import com.example.myhome.validator.RegistrationRequestValidator;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
@Slf4j
public class LoginController {

    @Autowired private ApplicationEventPublisher publisher;
    @Autowired private RegisterService registerService;
    @Autowired private RegistrationRequestValidator validator;
    @Autowired private LoginRequestValidator loginRequestValidator;
    @Autowired private AdminRepository adminRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private PersistentTokenRepository repository;
    @Autowired private ForgotPasswordTokenRepository passwordTokenRepository;

    @Autowired private EmailService emailService;

    @GetMapping("/cabinet/site/login")
    public String showLoginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "main_website/login";
    }

    @PostMapping("/cabinet/site/login")
    public String logInUser(@ModelAttribute LoginRequest loginRequest, BindingResult bindingResult,
                            Model model,
                            HttpServletRequest request) {
        loginRequestValidator.validate(loginRequest, bindingResult);
        if(bindingResult.hasErrors()) {
            log.info("Errors found in login request");
            log.info(bindingResult.getAllErrors().toString());
            return "main_website/login";
        }

        log.info("ya xz");
//        UsernamePasswordAuthenticationToken authReq
//                = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
//        Authentication auth = authManager.authenticate(authReq);
//
//        SecurityContext sc = SecurityContextHolder.getContext();
//        sc.setAuthentication(auth);
//        HttpSession session = request.getSession(true);
//        session.setAttribute(SPRING_SECURITY_CONTEXT_KEY, sc);

        return "redirect:/cabinet";
    }

    @GetMapping("/oauth2_login")
    public String oauthLogin() {
        return "oauth_login";
    }

    @GetMapping("/admin/site/login")
    public String showAdminLoginPage(Model model) {
        Object object = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if(!(object instanceof Admin)) return "main_website/admin_login";
        else {
            Admin admin = (Admin) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if(admin == null) return "main_website/admin_login";
            return "redirect:/admin";
        }
    }

    @GetMapping("/admin/forgot-password")
    public String showForgotPasswordPage(@RequestParam(required = false) String token) {
        if(token != null) {
            if(passwordTokenRepository.existsByToken(token)) {
                ForgotPasswordToken foundToken = passwordTokenRepository.findByToken(token).get();
                Admin admin = adminRepository.findByEmail(foundToken.getEmail()).get();
                return "redirect:/admin/reset-password?id="+admin.getId()+"&token="+token;
            }
            else return "main_website/admin_login";
        }
        return "main_website/forgot-password";
    }

    @PostMapping("/admin/forgot-password")
    public String forgotPasswordHandler(@RequestParam String username, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        String trimmedEmail = username.trim();
        if(!adminRepository.existsByEmail(trimmedEmail)) {
            redirectAttributes.addFlashAttribute("error", "Пользователя с такой почтой не существует!");
        } else {
            ForgotPasswordToken token = new ForgotPasswordToken(UUID.randomUUID().toString());
            token.setEmail(adminRepository.findByEmail(username).get().getEmail());
            passwordTokenRepository.save(token);
            String emailContent = "<a href=\"" + request.getRequestURL()+"?token="+token.getToken() + "\">Click on the link to reset your password</a>(something wrong w/ text encoding for cyrillic)";
            try {
                emailService.send(trimmedEmail, emailContent);
                redirectAttributes.addFlashAttribute("success", "Письмо отправлено на указанную почту!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "Ошибка при отправке письма на заданную почту!");
            }
        }
        return "redirect:/admin/forgot-password";
    }

    @GetMapping("/admin/reset-password")
    public String showResetPasswordPage(@RequestParam(required = false) Long id,
                                        @RequestParam(required = false) String token,
                                        Model model) {
        if(id == null ||
            token == null ||
            !passwordTokenRepository.existsByToken(token) ||
            !adminRepository.existsById(id)) return "redirect:/admin";

        //ебанутая проверка, если токен есть , но принадлежит другому пользователю
        if(!passwordTokenRepository.findByToken(token).get().getEmail()
                .equals(adminRepository.findById(id).get().getEmail())) return "redirect:/admin";

        model.addAttribute("id", id);
        model.addAttribute("token", token);
        return "main_website/reset-password";
    }

    @PostMapping("/admin/reset-password")
    public String resetPasswordHandler(@RequestParam Long id,
                                       @RequestParam String token,
                                       @RequestParam(required = false) String password,
                                       @RequestParam(required = false) String confirm_password,
                                       RedirectAttributes redirectAttributes) {
        //trim password
        if(password != null && confirm_password != null) {
            password = password.trim();
            confirm_password = confirm_password.trim();
        }
        //validation
        if(password == null || password.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Введите пароль!");
            return "redirect:/admin/reset-password?id="+id+"&token="+token;
        } else if(password.length() < 8 || password.length() > 100) {
            redirectAttributes.addFlashAttribute("error", "Длина пароля: 8-100");
            return "redirect:/admin/reset-password?id="+id+"&token="+token;
        } else if(confirm_password == null || confirm_password.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Подтвердите пароль!");
            return "redirect:/admin/reset-password?id="+id+"&token="+token;
        } else if(!confirm_password.equals(password)) {
            redirectAttributes.addFlashAttribute("error", "Пароли не совпадают!");
            return "redirect:/admin/reset-password?id="+id+"&token="+token;

        }
        //reset password and delete token if successful
        Admin admin = adminRepository.findById(id).get();
        admin.setPassword(passwordEncoder.encode(password));
        adminRepository.save(admin);

        passwordTokenRepository.findByToken(token).ifPresent(passwordTokenRepository::delete);

        //redirect to login
        redirectAttributes.addFlashAttribute("password_reset", "Пароль был успешно сброшен!");
        return "redirect:/admin/site/login";
    }

    @GetMapping("/cabinet/site/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("registrationRequest", new RegistrationRequest());
        return "main_website/register";
    }

    @PostMapping("/cabinet/site/register")
    public String registerUser(@ModelAttribute RegistrationRequest registrationRequest,
                               BindingResult bindingResult,
                               HttpServletRequest request) {
        validator.validate(registrationRequest, bindingResult);

        if(bindingResult.hasErrors()) {
            log.info("Errors found with reg.request");
            log.info(bindingResult.getAllErrors().toString());
            return "main_website/register";
        }
        registerService.register(registrationRequest);

        return "redirect:/cabinet/site/login";
    }

    @GetMapping("/cabinet/site/register/confirm")
    public String confirmRegister(@RequestParam String token) {
        log.info(token);
        if (token == null || !registerService.confirm(token)) {
            log.info("Wrong token, try again!");
        }
        return "redirect:/cabinet/site/login";
    }

    @GetMapping("/cabinet/logout")
    public String logout (HttpServletRequest request, HttpServletResponse response) {
        clearRememberMeCookie(request, response);
//        clearRememberMeTokens();
        SecurityContextHolder.getContext().setAuthentication(null);
        return "redirect:/cabinet/site/login";
    }

    @GetMapping("/admin/logout")
    public String adminLogout (HttpServletRequest request, HttpServletResponse response) {
        clearRememberMeCookie(request, response);
        SecurityContextHolder.getContext().setAuthentication(null);
        return "redirect:/admin/site/login";
    }

    void clearRememberMeCookie(HttpServletRequest request, HttpServletResponse response)
    {
        String cookieName = "remember-me";
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath(StringUtils.hasLength(request.getContextPath()) ? request.getContextPath() : "/");
        response.addCookie(cookie);
    }

}
