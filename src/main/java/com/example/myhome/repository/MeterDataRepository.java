package com.example.myhome.repository;

import com.example.myhome.model.MeterData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeterDataRepository extends JpaRepository<MeterData, Long>, JpaSpecificationExecutor<MeterData> {

    Optional<MeterData> findFirstByOrderByIdDesc();

    List<MeterData> findByApartmentId(long apartment_id);

    @Query("FROM MeterData md WHERE md.apartment.id=?1 AND md.service.id=?2")
    List<MeterData> findSingleMeterData(long apartment_id, long service_id);

    @Query("SELECT DISTINCT MAX(m.id) FROM MeterData m GROUP BY m.apartment, m.service")
    List<Long> findDistinctGroupByApartmentAndService();

    @Query("SELECT MAX(m.id) FROM MeterData m")
    Optional<Long> getMaxId();

    List<MeterData>findAllByApartmentId(Long id);

}
