package com.example.myhome.service.impl;


import com.example.myhome.exception.NotFoundException;
import com.example.myhome.model.Unit;
import com.example.myhome.repository.ServiceRepository;
import com.example.myhome.repository.UnitRepository;
import com.example.myhome.service.ServiceService;
import com.example.myhome.model.Service;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.Arrays;
import java.util.List;

@org.springframework.stereotype.Service
@Slf4j
public class ServiceServiceImpl implements ServiceService {

    @Autowired private ServiceRepository serviceRepository;
    @Autowired private UnitRepository unitRepository;

    @Override
    public List<Service> findAllServices() {return serviceRepository.findAll();}
    @Override
    public List<Unit> findAllUnits() {return unitRepository.findAll();}
    @Override
    public Service findServiceById(Long service_id) {return serviceRepository.findById(service_id).orElse(null);}
    @Override
    public String getServiceNameById(Long service_id) {return serviceRepository.findById(service_id).orElseThrow(NotFoundException::new).getName();}
    @Override
    public String getUnitNameById(Long unit_id) {return unitRepository.findById(unit_id).orElseThrow(NotFoundException::new).getName();}
    @Override
    public Service saveService(Service service) {return serviceRepository.save(service);}

    @Override
    public List<Service> saveServices(List<Service> list) {
        return serviceRepository.saveAll(list);
    }

    @Override
    public Unit saveUnit(Unit unit) {
        return unitRepository.save(unit);
    }

    @Override
    public List<Unit> saveUnits(List<Unit> list) {
        return unitRepository.saveAll(list);
    }

    @Override
    public void deleteServiceById(Long service_id) {serviceRepository.deleteById(service_id);}
    @Override
    public void deleteUnitById(Long unit_id) {unitRepository.deleteById(unit_id);}
    @Override
    public List<Unit> addNewUnits(List<Unit> unitList, String[] new_unit_names) {

        if(new_unit_names != null) {

            log.info(Arrays.toString(new_unit_names));

            for (int i = 0; i < new_unit_names.length-1; i++) {
                if(new_unit_names[i].isEmpty() || new_unit_names[i] == null) continue;
                log.info("creating new unit");
                Unit unit = new Unit();
                unit.setName(new_unit_names[i]);
                unitList.add(unit);
            }
        }

        log.info(unitList.toString());

        return unitRepository.saveAll(unitList);
    }
    @Override
    public List<Service> addNewServices(List<Service> serviceList,
                                        String[] new_service_names,
                                        String[] new_service_unit_names,
                                        String[] new_service_show_in_meters) {

        log.info(serviceList.toString());
        log.info("SERVICE NAMES: " + Arrays.toString(new_service_names));
        log.info("UNIT NAMES: " + Arrays.toString(new_service_unit_names));
        log.info("SHOW IN METERS: " + Arrays.toString(new_service_show_in_meters));

        for (int i = 0; i < new_service_names.length; i++) {
            if(new_service_names[i].isBlank() || new_service_unit_names[i].isBlank()) continue;
            Service service = new Service();
            service.setName(new_service_names[i]);
            service.setShow_in_meters(Boolean.parseBoolean(new_service_show_in_meters[i]));
            service.setUnit(unitRepository.findByName(new_service_unit_names[i]).orElseGet(Unit::new));
            serviceList.add(service);
        }

        return serviceRepository.saveAll(serviceList);
    }

}
