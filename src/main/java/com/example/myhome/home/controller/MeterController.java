package com.example.myhome.home.controller;

import com.example.myhome.home.model.MeterData;
import com.example.myhome.home.dto.MeterDataDTO;
import com.example.myhome.home.model.filter.FilterForm;
import com.example.myhome.home.service.ApartmentService;
import com.example.myhome.home.service.BuildingService;
import com.example.myhome.home.service.impl.MeterDataServiceImpl;
import com.example.myhome.home.service.impl.ServiceServiceImpl;
import com.example.myhome.home.validator.MeterValidator;
import com.example.myhome.util.MappingUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.logging.Filter;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/meters")
@Log
public class MeterController {

    @Autowired
    private MeterDataServiceImpl meterDataService;

    @Autowired
    private ServiceServiceImpl serviceService;

    @Autowired
    private BuildingService buildingService;

    @Autowired
    private ApartmentService apartmentService;

    @Autowired private MeterValidator validator;

    //Получить все счетчики
    @GetMapping
    public String showMetersPage(Model model,
                                 FilterForm form) {

        Page<MeterDataDTO> meterDataPage;
        Pageable pageable = PageRequest.of((form.getPage() == null) ? 1 : form.getPage()-1 ,5);

        meterDataPage = meterDataService.findAllByFiltersAndPage(form, pageable);

        model.addAttribute("meter_data_rows", meterDataPage.getContent());
        model.addAttribute("totalPagesCount", meterDataPage.getTotalPages());

        model.addAttribute("buildings", buildingService.findAllDTO());
        if(form.getBuilding() != null) model.addAttribute("sections", buildingService.findById(form.getBuilding()).getSections());
        model.addAttribute("services", serviceService.findAllServices());
        model.addAttribute("now", LocalDate.now());

        model.addAttribute("filter_form",form);

        return "admin_panel/meters/meters";
    }

    //Получить показания из какого-то одного счетчика
    @GetMapping("/data")
    public String showSingleMeterData(@RequestParam(required = false) Long flat_id,
                                      @RequestParam(required = false) Long service_id,
                                      Model model,
                                      FilterForm form) {

        form.setApartment(flat_id);
        form.setService(service_id);

        Pageable pageable = PageRequest.of((form.getPage() == null) ? 0 : form.getPage()-1 ,5);

        Page<MeterDataDTO> meterDataPage = meterDataService.findSingleMeterData(form, pageable);

        model.addAttribute("meter_data_rows", meterDataPage);
        model.addAttribute("filter_form", form);
        model.addAttribute("flat_id", flat_id);
        model.addAttribute("service_id", service_id);

        model.addAttribute("apart_number", apartmentService.getNumberById(flat_id));

        return "admin_panel/meters/meter_flat_data";
    }

    //Создать абсолютно новое показание
    @GetMapping("/create")
    public String showCreateMeterPage(Model model) {

        model.addAttribute("id",meterDataService.getMaxId()+1);
        model.addAttribute("meterDataDTO", new MeterDataDTO());
        model.addAttribute("services", serviceService.findAllServices());
        model.addAttribute("buildings", buildingService.findAll());
        model.addAttribute("now", LocalDate.now());

        return "admin_panel/meters/meter_card";
    }

    @PostMapping("/create")
    public String createMeter(@ModelAttribute MeterData meterData, BindingResult bindingResult, Model model) {
        validator.validate(meterData, bindingResult);
        log.info(bindingResult.getAllErrors().toString());
        if(bindingResult.hasErrors()) {
            model.addAttribute("id",meterDataService.getMaxId()+1);
            return "admin_panel/meters/meter_card";
        }
        MeterData savedMeter = meterDataService.saveMeterData(meterData);
        return "redirect:/admin/meters/data?flat_id="+savedMeter.getApartment().getId()+"&service_id="+savedMeter.getService().getId();
    }

    //Создать новое показание для существующего счетчика
    @GetMapping("/create-add")
    public String showCreateAdditionalMeterPage(@RequestParam long flat_id, @RequestParam long service_id, Model model) {
        List<MeterData> meterDataList = meterDataService.findSingleMeterData(flat_id, service_id);
        MeterData meter = (meterDataList.isEmpty()) ? new MeterData() : meterDataList.get(meterDataList.size()-1);
        meter.setId(null);
        model.addAttribute("id",meterDataService.getMaxId()+1);
        model.addAttribute("meterDataDTO", MappingUtils.fromMeterToDTO(meter));
        model.addAttribute("services", serviceService.findAllServices());
        model.addAttribute("buildings", buildingService.findAll());
        model.addAttribute("now", LocalDate.now());
        return "admin_panel/meters/meter_card";
    }

    @PostMapping("/create-add")
    public String alo(@Valid @ModelAttribute MeterData meterData) {
        MeterData savedMeter = meterDataService.saveMeterData(meterData);
        return "redirect:/admin/meters/data?flat_id="+savedMeter.getApartment().getId()+"&service_id="+savedMeter.getService().getId();
    }

    @PostMapping("/save-meter")
    public @ResponseBody List<String> saveMeter(@RequestParam(required = false) Long id,    // <-- если хочешь обновить существующий
                                              @RequestParam String building,
                                              @RequestParam String section,
                                              @RequestParam String apartment,
                                              @RequestParam String currentReadings,
                                              @RequestParam String status,
                                              @RequestParam String service,
                                              @RequestParam String date) {

        MeterData meterToSave = meterDataService.saveMeterDataAJAX(id, building, section,
                apartment, currentReadings, status, service, date);
        log.info(meterToSave.toString());
        DataBinder binder = new DataBinder(meterToSave);
        binder.setValidator(validator);
        binder.validate();
        if(binder.getBindingResult().hasErrors()) {
            List<ObjectError> messages = binder.getBindingResult().getAllErrors();
            List<String> msgs =
                    messages.stream()
                            .map(ObjectError::getDefaultMessage).filter(Objects::nonNull)
                            .map(str -> {
                                byte[] utf8bytes = str.getBytes(StandardCharsets.UTF_8);
                                return new String(utf8bytes, StandardCharsets.UTF_8);
                            })
                            .collect(Collectors.toList());
            log.info(msgs.toString());
            log.info(String.valueOf(msgs.size()));
            return msgs;
        }
        meterDataService.saveMeterData(meterToSave);
        return null;
    }




    @GetMapping("/update/{id}")
    public String showUpdateMeterPage(@PathVariable long id, Model model) {
        MeterData meter = meterDataService.findMeterDataById(id);
        log.info(meter.toString());
        log.info(MappingUtils.fromMeterToDTO(meter).toString());
        model.addAttribute("meterDataDTO", MappingUtils.fromMeterToDTO(meter));
        model.addAttribute("id",meter.getId());
        model.addAttribute("services", serviceService.findAllServices());
        model.addAttribute("building", MappingUtils.fromBuildingToDTO(meter.getBuilding()));
        model.addAttribute("buildings", buildingService.findAll());
        model.addAttribute("now", LocalDate.now());

        return "admin_panel/meters/meter_card";
    }

    @PostMapping("/update/{id}")
    public String updateMeter(@PathVariable long id, @ModelAttribute MeterData meterData, BindingResult bindingResult, Model model) {
        validator.validate(meterData, bindingResult);
        log.info(bindingResult.getAllErrors().toString());
        if(bindingResult.hasErrors()) {
            model.addAttribute("id",meterDataService.getMaxId()+1);
            return "admin_panel/meters/meter_card";
        }
        MeterData savedMeter = meterDataService.saveMeterData(meterData);
        return "redirect:/admin/meters/data?flat_id="+savedMeter.getApartment().getId()+"&service_id="+savedMeter.getService().getId();
    }

    @GetMapping("/delete/{id}")
    public String deleteMeter(@PathVariable long id,
                              @RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer) {
        meterDataService.deleteMeterById(id);
        return "redirect:" + referrer;
    }

    @GetMapping("/info/{id}")
    public String showInfo(@PathVariable long id, Model model) {
        model.addAttribute("meter", MappingUtils.fromMeterToDTO(meterDataService.findMeterDataById(id)));
        return "admin_panel/meters/meter_profile";
    }

    @GetMapping("/get-meters")
    public @ResponseBody Page<MeterDataDTO> getMeters(@RequestParam Integer page,
                                                      @RequestParam Integer size,
                                                      @RequestParam String filters) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        FilterForm form = mapper.readValue(filters, FilterForm.class);
        return meterDataService.findAllBySpecification(form, page, size);
    }

    @ModelAttribute
    public void addAttributes(Model model) {
    }


}
