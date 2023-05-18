package com.example.myhome.service.impl;

import com.example.myhome.exception.NotFoundException;
import com.example.myhome.model.InvoiceComponents;
import com.example.myhome.repository.InvoiceComponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
public class InvoiceComponentServiceImpl {
    @Autowired
    private InvoiceComponentRepository invoiceComponentRepository;


    public List<InvoiceComponents> findAll() {
        return invoiceComponentRepository.findAll();
    }

    public InvoiceComponents findAdminById(Long id) {
        return invoiceComponentRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public InvoiceComponents save(InvoiceComponents invoiceComponents) {
        return invoiceComponentRepository.save(invoiceComponents);
    }

    public void deleteById(Long id) {
        invoiceComponentRepository.deleteById(id);
    }

    public Map<String, Double> findExprncesLastMonthByApartment(Long id) {
        Integer year = LocalDate.now().getYear();
        Integer lastMonthValue = LocalDate.now().minusMonths(1).getMonthValue();
        Integer lastDayOfMonth =  LocalDate.now().minusMonths(1).with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
        LocalDate from = LocalDate.of(year, lastMonthValue, 01);
        LocalDate to = LocalDate.of(year, lastMonthValue, lastDayOfMonth);
//        System.out.println("даты за посл месяц "+ from + " - " + to);
        List<InvoiceComponents> invoiceComponentsList = invoiceComponentRepository.findByInvoice_Apartment_IdAndInvoice_DateBetween(id, from, to);

        Map<String, Double> resultMap = new HashMap<>();
        for (InvoiceComponents item : invoiceComponentsList) {
            String name = item.getService().getName();
            Double value = item.getTotalPrice();
            if (resultMap.containsKey(name)) {
                Double sum = resultMap.get(name) + value;
                resultMap.put(name, sum);
            } else {
                resultMap.put(name, value);
            }
        }
//        List<String> names = new ArrayList<>(resultMap.keySet());
//        List<Double> values = new ArrayList<>(resultMap.values());
        return resultMap;
    }

    public Map<String, Double> findExprncesThisYearByApartment(Long id) {
        Integer yearValue = LocalDate.now().getYear();
        LocalDate from = LocalDate.of(yearValue, 01, 01);
        LocalDate to = LocalDate.now();
//        System.out.println("даты за посл год "+ from + " - " + to);
        List<InvoiceComponents> invoiceComponentsList = invoiceComponentRepository.findByInvoice_Apartment_IdAndInvoice_DateBetween(id, from, to);

        Map<String, Double> resultMap = new HashMap<>();
        for (InvoiceComponents item : invoiceComponentsList) {
            String name = item.getService().getName();
            Double value = item.getTotalPrice();
            if (resultMap.containsKey(name)) {
                Double sum = resultMap.get(name) + value;
                resultMap.put(name, sum);
            } else {
                resultMap.put(name, value);
            }
        }
//        List<String> names = new ArrayList<>(resultMap.keySet());
//        List<Double> values = new ArrayList<>(resultMap.values());
        return resultMap;
    }
}
