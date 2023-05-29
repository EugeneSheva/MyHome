package com.example.myhome.service;

import com.example.myhome.dto.MeterDataDTO;
import com.example.myhome.model.MeterData;
import com.example.myhome.model.filter.FilterForm;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface MeterDataService {

    List<MeterData> findAllMeters();
    List<MeterData> findSingleMeterData(Long apartment_id, Long service_id);
    Page<MeterDataDTO> findSingleMeterData(FilterForm form, Pageable pageable);
    Page<MeterDataDTO> findAllByFiltersAndPage(FilterForm filters, Pageable pageable);

    MeterData findMeterDataById(Long meter_id);
    MeterDataDTO findMeterDataDTOById(Long meter_id);

    Long getMaxId();

    MeterData saveMeterData(MeterData meterData);
    MeterData saveMeterData(MeterDataDTO dto);
    MeterData saveMeterDataAJAX(Long id, String building, String section, String apartment, String currentReadings, String status, String service, String date);

    void deleteMeterById(Long meter_id);

    Specification<MeterData> buildSpecFromFilters(FilterForm filterForm);

    Page<MeterDataDTO> findAllBySpecification(FilterForm form, Integer page, Integer size);

    List<MeterData> findAllByApartmentId(Long id);
}
