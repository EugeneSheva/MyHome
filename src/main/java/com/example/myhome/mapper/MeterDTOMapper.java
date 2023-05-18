package com.example.myhome.mapper;

import com.example.myhome.dto.MeterDataDTO;
import com.example.myhome.model.MeterData;
import com.example.myhome.model.MeterPaymentStatus;
import org.springframework.stereotype.Component;

@Component
public class MeterDTOMapper {

    public MeterData fromDTOToMeter(MeterDataDTO dto) {
        if(dto == null) return null;
        MeterData meter = new MeterData();
        meter.setId(dto.getId());
        meter.setCurrentReadings(dto.getReadings());
        meter.setSection(dto.getSection());
        meter.setStatus(MeterPaymentStatus.valueOf(dto.getStatus()));

        return meter;
    }
    public MeterDataDTO fromMeterToDTO(MeterData meter) {
        if(meter == null) return null;

        MeterDataDTO dto = new MeterDataDTO();

        dto.setId(meter.getId());
        dto.setReadings(meter.getCurrentReadings());
        dto.setApartmentID(meter.getApartment().getId());
        dto.setApartmentNumber(meter.getApartment().getNumber());
        dto.setBuildingID(meter.getBuilding().getId());
        dto.setBuildingName(meter.getBuilding().getName());
        dto.setServiceID(meter.getService().getId());
        dto.setServiceName(meter.getService().getName());
        dto.setServiceUnitName(meter.getService().getUnit().getName());
        dto.setSection(meter.getSection());
        dto.setStatus(meter.getStatus().getName());
        dto.setDate(meter.getDate());
        dto.setApartmentOwnerID(meter.getApartment().getOwner().getId());
        dto.setApartmentOwnerFullName(meter.getApartment().getOwner().getFullName());

        return dto;
    }

}
