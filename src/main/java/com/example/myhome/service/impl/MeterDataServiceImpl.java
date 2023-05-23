package com.example.myhome.service.impl;

import com.example.myhome.dto.MeterDataDTO;
import com.example.myhome.mapper.MeterDTOMapper;
import com.example.myhome.model.Building;
import com.example.myhome.model.MeterPaymentStatus;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.repository.ApartmentRepository;
import com.example.myhome.repository.BuildingRepository;
import com.example.myhome.repository.MeterDataRepository;
import com.example.myhome.repository.ServiceRepository;
import com.example.myhome.service.ApartmentService;
import com.example.myhome.service.BuildingService;
import com.example.myhome.service.MeterDataService;
import com.example.myhome.service.ServiceService;
import com.example.myhome.specification.MeterSpecifications;
import com.example.myhome.validator.MeterValidator;
import com.example.myhome.model.Apartment;
import com.example.myhome.model.MeterData;
import com.example.myhome.util.MappingUtils;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log
public class MeterDataServiceImpl implements MeterDataService {

    @Autowired
    private MeterDataRepository meterDataRepository;

    @Autowired private ApartmentService apartmentService;
    @Autowired private ServiceService serviceService;
    @Autowired private BuildingService buildingService;

    @Autowired private ApartmentRepository apartmentRepository;
    @Autowired private ServiceRepository serviceRepository;
    @Autowired private BuildingRepository buildingRepository;

    @Autowired private MeterValidator validator;
    @Autowired private MeterDTOMapper mapper;

    @Override
    public List<MeterData> findAllMeters() {return meterDataRepository.findAll();}
    @Override
    public List<MeterData> findSingleMeterData(Long apartment_id, Long service_id) {
        if(service_id != null) return meterDataRepository.findSingleMeterData(apartment_id, service_id);
        else return meterDataRepository.findByApartmentId(apartment_id);
    }
    @Override
    public Page<MeterDataDTO> findSingleMeterData(FilterForm form, Pageable pageable) {
        log.info("Searching for single meter data (Apartment ID: " + form.getApartment() + ", Service ID: " + form.getService() + ")");

        MeterPaymentStatus status = (form.getStatus() != null && !form.getStatus().isEmpty()) ? MeterPaymentStatus.valueOf(form.getStatus()) : null;
        String date = form.getDate();
        LocalDate date_from = null;
        LocalDate date_to = null;
        if(date != null && !date.isEmpty()) {
            date_from = LocalDate.parse(date.split(" to ")[0]);
            date_to = LocalDate.parse(date.split(" to ")[1]);
        }
        Specification<MeterData> spec = Specification.where(MeterSpecifications.hasApartment(apartmentRepository.getReferenceById(form.getApartment()))
                                                    .and(MeterSpecifications.hasService(serviceRepository.getReferenceById(form.getService())))
                                                    .and(MeterSpecifications.hasId(form.getId()))
                                                    .and(MeterSpecifications.hasStatus(status))
                                                    .and(MeterSpecifications.datesBetween(date_from, date_to)));

        Page<MeterData> initialPage = meterDataRepository.findAll(spec, pageable);
        log.info("Found " + initialPage.getContent().size() + " elements(page " + pageable.getPageNumber() + "/ " + initialPage.getTotalPages() + ")");
        List<MeterDataDTO> listDTO = initialPage.getContent().stream().map(mapper::fromMeterToDTO).collect(Collectors.toList());
        return new PageImpl<>(listDTO, pageable, initialPage.getTotalElements());
    }
    @Override
    public Page<MeterDataDTO> findAllByFiltersAndPage(FilterForm filters, Pageable pageable) {
        log.info("Searching for all meters with specified filters: " + filters.toString());
        Page<MeterData> initialPage = meterDataRepository.findAll(buildSpecFromFilters(filters), pageable);
        log.info("Found " + initialPage.getContent().size() + " elements(page " + pageable.getPageNumber() + "/ " + initialPage.getTotalPages() + ")");
        List<MeterDataDTO> listDTO = initialPage.getContent().stream().map(MappingUtils::fromMeterToDTO).collect(Collectors.toList());
        return new PageImpl<>(listDTO, pageable, initialPage.getTotalElements());
    }

    @Override
    public Specification<MeterData> buildSpecFromFilters(FilterForm form) {

        log.info("Building specification with specified filters: " + form.toString());

        Long id = form.getId();
        MeterPaymentStatus status = form.getStatus() != null ? MeterPaymentStatus.valueOf(form.getStatus()) : null;
        String date = form.getDate();
        LocalDate date_from = null;
        LocalDate date_to = null;
        if(date != null && !date.isEmpty()) {
            date_from = LocalDate.parse(date.split(" to ")[0]);
            date_to = LocalDate.parse(date.split(" to ")[1]);
        }
        Building building = (form.getBuilding() != null) ? buildingService.findById(form.getBuilding()) : null;
        String section = form.getSection();
        Apartment apartment = (form.getApartment() != null) ? apartmentService.findByNumber(form.getApartment()) : null;
        com.example.myhome.model.Service service = (form.getService() != null) ? serviceService.findServiceById(form.getService()) : null;

        Specification<MeterData> spec = Specification.where(MeterSpecifications.hasId(id)
                                                        .and(MeterSpecifications.hasStatus(status))
                                                        .and(MeterSpecifications.datesBetween(date_from, date_to))
                                                        .and(MeterSpecifications.hasBuilding(building))
                                                        .and(MeterSpecifications.hasSection(section))
                                                        .and(MeterSpecifications.hasApartment(apartment))
                                                        .and(MeterSpecifications.hasService(service))
                                                        .and(MeterSpecifications.groupTest()));

        log.info("Specification ready!");

        return spec;
    }

    public Page<MeterDataDTO> findAllBySpecification(FilterForm filters, Integer page, Integer size) {

        Specification<MeterData> spec = buildSpecFromFilters(filters);
        Pageable pageable = PageRequest.of(page-1, size);

        Page<MeterData> initialPage = meterDataRepository.findAll(spec, pageable);

        List<MeterDataDTO> listDTO = initialPage.getContent().stream()
                .map(mapper::fromMeterToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(listDTO, pageable, initialPage.getTotalElements());
    }

    @Override
    public Long getMaxId() {return meterDataRepository.getMaxId().orElse(0L);}

    @Override
    public MeterData findMeterDataById(Long meter_id) {return meterDataRepository.findById(meter_id).orElseThrow();}

    public MeterDataDTO findMeterDataDTOById(Long meter_id) {
        MeterData meter = meterDataRepository.findById(meter_id).orElseThrow();
        return mapper.fromMeterToDTO(meter);
    }

    @Override
    public MeterData saveMeterData(MeterData meterData) {return meterDataRepository.save(meterData);}
    public MeterData saveMeterData(MeterDataDTO dto) {
        log.info("Forming meter to save from DTO");
        MeterData meterData = mapper.fromDTOToMeter(dto);
        meterData.setApartment(apartmentRepository.getReferenceById(dto.getApartmentID()));
        meterData.setBuilding(buildingRepository.getReferenceById(dto.getBuildingID()));
        meterData.setService(serviceRepository.getReferenceById(dto.getServiceID()));
        return saveMeterData(meterData);
    }
    public MeterData saveMeterDataAJAX(Long id,    // <-- если хочешь обновить существующий
                                       String building_id,
                                       String section_name,
                                       String apartment_id,
                                       String readings,
                                       String stat,
                                       String service_id,
                                       String date) {

        MeterData newMeter = (id == null || id.equals(getMaxId()+1)) ? new MeterData() : findMeterDataById(id);
        try {
            newMeter.setBuilding((building_id != null) ? buildingService.findById(Long.parseLong(building_id)) : null);
            newMeter.setSection(section_name);
            newMeter.setApartment((apartment_id != null) ? apartmentService.findById(Long.parseLong(apartment_id)) : null);
            newMeter.setCurrentReadings((readings != null) ? Double.parseDouble(readings) : null);
            newMeter.setStatus(MeterPaymentStatus.valueOf(stat));
            newMeter.setService((service_id != null) ? serviceService.findServiceById(Long.parseLong(service_id)) : null);
            newMeter.setDate((date != null) ? LocalDate.parse(date) : null);
        } catch (Exception e) {
            log.info("Exception while creating meter");
            log.info(Arrays.toString(e.getStackTrace()));
        }

        return newMeter;
    }

    @Override
    public void deleteMeterById(Long meter_id) {meterDataRepository.deleteById(meter_id);}


}
