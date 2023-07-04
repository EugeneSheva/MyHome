package com.example.myhome.controller;

import com.example.myhome.model.Service;
import com.example.myhome.model.ServiceForm;
import com.example.myhome.model.Unit;
import com.example.myhome.service.ServiceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.ConstraintViolationException;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/services")
@RequiredArgsConstructor
@Log
public class ServiceController {

    private final ServiceService serviceService;
    private final MessageSource messageSource;
    private final Validator validator;

    // Открыть страничку настройки услуг и ед.изм.
    @GetMapping
    public String showServicesPage(Model model){
        ServiceForm serviceForm = new ServiceForm();
        serviceForm.setServiceList(serviceService.findAllServices());
        serviceForm.setUnitList(serviceService.findAllUnits());
        model.addAttribute("serviceForm", serviceForm);
        model.addAttribute("units", serviceService.findAllUnits());
        model.addAttribute("totalServiceCount",serviceForm.getServiceList().size());
        model.addAttribute("totalUnitCount",serviceForm.getUnitList().size());

        model.addAttribute("servicesPageActive", true);
        model.addAttribute("settingsEditPageActive", true);

        return "admin_panel/system_settings/settings_services";
    }


    // Сохранить все услуги/единицы измерения
    // Список всех имеющихся услуг и единиц привязывается к объекту ServiceForm
    // Затем в этот список добавляются и сохраняются новые услуги/единицы через соотв.массивы
    @PostMapping
    public String updateServices(@Valid @ModelAttribute ServiceForm serviceForm, BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        List<Service> serviceList = serviceForm.getServiceList();
        List<Unit> unitList = serviceForm.getUnitList();
        log.info(serviceList.toString());
        log.info(unitList.toString());

        if(bindingResult.hasErrors()) {
            log.info("ERRORS FOUND");
            log.info(bindingResult.getAllErrors().toString());
            model.addAttribute("validation", "failed");
            model.addAttribute("totalServiceCount",serviceForm.getServiceList().size());
            model.addAttribute("totalUnitCount",serviceForm.getUnitList().size());
            return "admin_panel/system_settings/settings_services";
        }

        try {
            serviceService.saveUnits(unitList);
            serviceService.saveServices(serviceList);
        } catch (ConstraintViolationException e) {
            log.info("serviceform validation failed");
            model.addAttribute("validation", "failed");
            return "admin_panel/system_settings/settings_services";
        } catch (Exception e) {
            log.info("validation not failed, but other error during saving");
            log.info(e.getMessage());
            redirectAttributes.addFlashAttribute("fail", "error during saving");
            return "admin_panel/system_settings/settings_services";
        }

        return "redirect:/admin/services";
    }

    // Удалить услугу по её ID
    @GetMapping("/delete/{id}")
    public String deleteService(@PathVariable long id, RedirectAttributes redirectAttributes) {
        try {
            serviceService.deleteServiceById(id);
            return "redirect:/admin/services";
        } catch (Exception e) {
            e.printStackTrace();
            String part1 = messageSource.getMessage("settings.system.services.delete.error.1", null, LocaleContextHolder.getLocale());
            String part2 = messageSource.getMessage("settings.system.services.delete.error.2", null, LocaleContextHolder.getLocale());
            String fail_msg = part1 + "\"" + serviceService.getServiceNameById(id) +"\"," + part2;
            redirectAttributes.addFlashAttribute("fail", fail_msg);
            return "redirect:/admin/services";
        }
    }

    // Удалить единицу измерения по её ID
    @GetMapping("/delete-unit/{id}")
    public String deleteServiceUnit(@PathVariable long id, RedirectAttributes redirectAttributes) {
        try {
            serviceService.deleteUnitById(id);
            return "redirect:/admin/services";
        } catch (Exception e) {
            e.printStackTrace();
            String part1 = messageSource.getMessage("settings.system.units.delete.error.1", null, LocaleContextHolder.getLocale());
            String part2 = messageSource.getMessage("settings.system.services.delete.error.2", null, LocaleContextHolder.getLocale());
            String fail_msg = part1 + "\"" + serviceService.getUnitNameById(id) +"\"," + part2;
            redirectAttributes.addFlashAttribute("fail", fail_msg);
            return "redirect:/admin/services";
        }
    }

    // Получить конкретную единицу измерения по её ID
    @GetMapping("/get-unit")
    public @ResponseBody Unit getUnitFromService(@RequestParam long id) {
        return serviceService.findServiceById(id).getUnit();
    }
}
