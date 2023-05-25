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
        dto.setApartmentID((meter.getApartment() != null) ? meter.getApartment().getId() : 0);
        dto.setApartmentNumber((meter.getApartment() != null) ? meter.getApartment().getNumber() : 0);
        dto.setBuildingID((meter.getBuilding() != null) ? meter.getBuilding().getId() : 0);
        dto.setBuildingName((meter.getBuilding() != null) ? meter.getBuilding().getName() : "N/A");
        dto.setServiceID((meter.getService() != null) ? meter.getService().getId() : 0);
        dto.setServiceName((meter.getService() != null) ? meter.getService().getName() : "N/A");
        dto.setServiceUnitName((meter.getService() != null && meter.getService().getUnit() != null) ?
                meter.getService().getUnit().getName() : "N/A");
        dto.setSection(meter.getSection());
        dto.setStatus((meter.getStatus() != null) ? meter.getStatus().getName() : null);
        dto.setDate(meter.getDate());
        dto.setApartmentOwnerID((meter.getApartment() != null && meter.getApartment().getOwner() != null) ? meter.getApartment().getOwner().getId() : 0);
        dto.setApartmentOwnerFullName((meter.getApartment() != null && meter.getApartment().getOwner() != null) ? meter.getApartment().getOwner().getFullName() : "N/A");

        return dto;
    }

}
