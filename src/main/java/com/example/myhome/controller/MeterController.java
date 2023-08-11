package com.example.myhome.controller;

import com.example.myhome.dto.MeterDataDTO;
import com.example.myhome.mapper.MeterDTOMapper;
import com.example.myhome.model.MeterData;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.service.ApartmentService;
import com.example.myhome.service.BuildingService;
import com.example.myhome.service.MeterDataService;
import com.example.myhome.service.ServiceService;

import com.example.myhome.validator.MeterValidator;
import com.example.myhome.util.MappingUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
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

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/meters")
@RequiredArgsConstructor
@Slf4j
public class MeterController {

    private final MeterDataService meterDataService;
    private final ServiceService serviceService;
    private final BuildingService buildingService;
    private final ApartmentService apartmentService;

    private final MeterValidator validator;
    private final MeterDTOMapper mapper;

    // Открыть страничку показаний всех счетчиков (сгруппированных по ID квартиры+услуги)
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

    // Открыть страничку показаний какого-то одного счетчика(ID квартиры + ID услуги)
    @GetMapping("/data")
    public String showSingleMeterData(@RequestParam(required = false) Long flat_id,
                                      @RequestParam(required = false) Long service_id,
                                      Model model,
                                      FilterForm form) {

        form.setApartment(flat_id);
        form.setService(service_id);

        Pageable pageable = PageRequest.of((form.getPage() == null) ? 0 : form.getPage()-1 ,5);

        Page<MeterDataDTO> meterDataPage = meterDataService.findSingleMeterData(form, pageable);
        log.info(meterDataPage.getContent().toString());
        model.addAttribute("meter_data_rows", meterDataPage);
        model.addAttribute("filter_form", form);
        model.addAttribute("flat_id", flat_id);
        model.addAttribute("service_id", service_id);

        model.addAttribute("services", serviceService.findAllServices());

        model.addAttribute("apart_number", apartmentService.getNumberById(flat_id));

        return "admin_panel/meters/meter_flat_data";
    }

    // Открыть страницу профиля конкретного показания
    @GetMapping("/info/{id}")
    public String showInfo(@PathVariable long id, Model model) {
        model.addAttribute("meter", meterDataService.findMeterDataDTOById(id));
        return "admin_panel/meters/meter_profile";
    }

    // Открыть страничку создания показания
    @GetMapping("/create")
    public String showCreateMeterPage(Model model) {

        model.addAttribute("id",meterDataService.getMaxId()+1);
        model.addAttribute("meterDataDTO", new MeterDataDTO());
        model.addAttribute("services", serviceService.findAllServices());
        model.addAttribute("buildings", buildingService.findAllDTO());
        model.addAttribute("now", LocalDate.now());

        return "admin_panel/meters/meter_card";
    }

    // Открыть страницу создания дополнительного нового показания для существующего счетчика
    @GetMapping("/create-add")
    public String showCreateAdditionalMeterPage(@RequestParam long flat_id, @RequestParam long service_id, Model model) {
        List<MeterData> meterDataList = meterDataService.findSingleMeterData(flat_id, service_id);
        MeterData meter = (meterDataList.isEmpty()) ? new MeterData() : meterDataList.get(meterDataList.size()-1);
        meter.setId(null);

        model.addAttribute("id",meterDataService.getMaxId()+1);
        model.addAttribute("meterDataDTO", mapper.fromMeterToDTO(meter));
        model.addAttribute("services", serviceService.findAllServices());
        model.addAttribute("buildings", buildingService.findAll());
        model.addAttribute("building", buildingService.findBuildingDTObyId(meter.getBuilding().getId()));
        model.addAttribute("now", LocalDate.now());
        return "admin_panel/meters/meter_card";
    }

    // Открытие странички обновления показания
    @GetMapping("/update/{id}")
    public String showUpdateMeterPage(@PathVariable long id, Model model) {
        MeterDataDTO meter = meterDataService.findMeterDataDTOById(id);
        log.info(meter.toString());
        model.addAttribute("meterDataDTO", meter);
        model.addAttribute("id",meter.getId());
        model.addAttribute("services", serviceService.findAllServices());
        model.addAttribute("building", buildingService.findBuildingDTObyId(meter.getBuildingID()));
        model.addAttribute("buildings", buildingService.findAll());
        model.addAttribute("now", LocalDate.now());
        System.out.println(meter.getSection());
        System.out.println(buildingService.findBuildingDTObyId(meter.getBuildingID()));
        return "admin_panel/meters/meter_card";
    }

    // Сохранить созданное показание
    @PostMapping("/create")
    public String createMeter(@ModelAttribute MeterDataDTO meterDataDTO,BindingResult bindingResult, Model model) {
        log.info(meterDataDTO.toString());
        validator.validate(meterDataDTO, bindingResult);
        log.info(bindingResult.getAllErrors().toString());
        if(bindingResult.hasErrors()) {
            model.addAttribute("id",meterDataService.getMaxId()+1);
            if(meterDataDTO.getBuildingID() != null && meterDataDTO.getBuildingID() != 0) model.addAttribute("building", buildingService.findBuildingDTObyId(meterDataDTO.getBuildingID()));
            model.addAttribute("services", serviceService.findAllServices());
            model.addAttribute("buildings", buildingService.findAllDTO());
            model.addAttribute("validation", "failed");
//            model.addAttribute("meterDataDTO", meterDataDTO);
            return "admin_panel/meters/meter_card";
        }
        MeterData savedMeter = meterDataService.saveMeterData(meterDataDTO);
        return "redirect:/admin/meters/data?flat_id="+savedMeter.getApartment().getId()+"&service_id="+savedMeter.getService().getId();
    }

    // Сохранить созданное дополнительное показание
    @PostMapping("/create-add")
    public String alo(@ModelAttribute MeterDataDTO meterDataDTO,BindingResult bindingResult,Model model) {
        validator.validate(meterDataDTO, bindingResult);
        log.info(bindingResult.getAllErrors().toString());
        if(bindingResult.hasErrors()) {
            model.addAttribute("id",meterDataService.getMaxId()+1);
            model.addAttribute("building", buildingService.findBuildingDTObyId(meterDataDTO.getBuildingID()));
            model.addAttribute("services", serviceService.findAllServices());
            model.addAttribute("buildings", buildingService.findAllDTO());
            model.addAttribute("validation", "failed");
            return "admin_panel/meters/meter_card";
        }
        MeterData savedMeter = meterDataService.saveMeterData(meterDataDTO);
        return "redirect:/admin/meters/data?flat_id="+savedMeter.getApartment().getId()+"&service_id="+savedMeter.getService().getId();
    }

    // Сохранение обновленного показания
    @PostMapping("/update/{id}")
    public String updateMeter(@PathVariable long id, @ModelAttribute MeterDataDTO meterDataDTO, BindingResult bindingResult, Model model) {
        validator.validate(meterDataDTO, bindingResult);
        log.info(bindingResult.getAllErrors().toString());
        if(bindingResult.hasErrors()) {
            model.addAttribute("id",meterDataService.getMaxId()+1);
            model.addAttribute("validation", "failed");
            return "admin_panel/meters/meter_card";
        }
        MeterData savedMeter = meterDataService.saveMeterData(meterDataDTO);
        return "redirect:/admin/meters/data?flat_id="+savedMeter.getApartment().getId()+"&service_id="+savedMeter.getService().getId();
    }

    // Удаление показания счетчика
    @GetMapping("/delete/{id}")
    public String deleteMeter(@PathVariable long id,
                              @RequestHeader(value = HttpHeaders.REFERER, required = false) final String referrer) {
        meterDataService.deleteMeterById(id);
        return "redirect:" + referrer;
    }

    // Сохранение показания через AJAX (для кнопки Сохранить и создать новое показание)
    @PostMapping("/save-meter")
    public @ResponseBody List<String> saveMeter(@RequestParam(required = false) Long id,    // <-- если хочешь обновить существующий
                                              @RequestParam String building,
                                              @RequestParam String section,
                                              @RequestParam String apartment,
                                              @RequestParam String currentReadings,
                                              @RequestParam String status,
                                              @RequestParam String service,
                                              @RequestParam String date) {

        log.info(status);
        MeterData meterToSave = meterDataService.saveMeterDataAJAX(id, building, section,
                apartment, currentReadings, status, service, date);
        MeterDataDTO dto = mapper.fromMeterToDTO(meterToSave);
        log.info(meterToSave.toString());
        log.info(dto.toString());
        DataBinder binder = new DataBinder(dto);
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

    // Получение всех показаний счетчиков через AJAX
    @GetMapping("/get-meters")
    public @ResponseBody Page<MeterDataDTO> getMeters(@RequestParam Integer page,
                                                      @RequestParam Integer size,
                                                      @RequestParam String filters) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        FilterForm form = mapper.readValue(filters, FilterForm.class);
        return meterDataService.findAllBySpecification(form, page, size);
    }

    // Получение показаний одного счетчика через AJAX
    @GetMapping("/get-meter-data")
    public @ResponseBody Page<MeterDataDTO> getMeterData(@RequestParam Integer page,
                                                         @RequestParam Integer size,
                                                         @RequestParam String filters) throws JsonProcessingException {
        log.info("method triggered");
        ObjectMapper mapper = new ObjectMapper();
        FilterForm form = mapper.readValue(filters, FilterForm.class);
        return meterDataService.findSingleMeterData(form, PageRequest.of(page-1, size));
    }

    @ModelAttribute
    public void addAttributes(Model model) {
        model.addAttribute("metersPageActive", true);
    }


}
