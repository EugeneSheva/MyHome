package com.example.myhome.service;

import com.example.myhome.model.Service;
import com.example.myhome.model.Unit;

import java.util.List;

public interface ServiceService {

    List<Service> findAllServices();
    List<Unit> findAllUnits();

    Service findServiceById(Long service_id);
    String getServiceNameById(Long service_id);
    String getUnitNameById(Long unit_id);

    Service saveService(Service service);
    List<Service> saveServices(List<Service> list);
    Unit saveUnit(Unit unit);
    List<Unit> saveUnits(List<Unit> list);
    void deleteServiceById(Long service_id);
    void deleteUnitById(Long unit_id);

    List<Unit> addNewUnits(List<Unit> unitList, String[] new_unit_names);
    List<Service> addNewServices(List<Service> serviceList,
                                 String[] new_service_names,
                                 String[] new_service_unit_names,
                                 String[] new_service_show_in_meters);

}
