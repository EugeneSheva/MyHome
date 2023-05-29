package com.example.myhome.service;

import com.example.myhome.dto.ApartmentDTO;
import com.example.myhome.dto.OwnerDTO;
import com.example.myhome.model.Owner;
import com.example.myhome.model.filter.FilterForm;
import com.example.myhome.util.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface OwnerService {
    OwnerDTO findByIdDTO(Long id);

    Owner findById(Long id);

    Owner findByLogin(String login);

    List<Owner> findAll();

    Page<Owner> findAll(Pageable pageable);

    List<ApartmentDTO> findOwnerApartments(Long ownerID);

    List<OwnerDTO> findAllDTO();

    List<OwnerDTO> getOwnerDTOByPage(String name, int page_number);

    Page<OwnerDTO> findAllDTO(Pageable pageable);

    Page<OwnerDTO> findAllBySpecification(FilterForm filters, Integer page, Integer size);

    Specification<Owner> buildSpecFromFilters(FilterForm filters);

    Page<OwnerDTO> findAllBySpecification2(FilterForm filters, Integer page, Integer size);

    Page<OwnerDTO> findByNameFragmentDTO(String name, Pageable pageable);

    Owner save(Owner owner);
    Owner save(OwnerDTO dto);

    void deleteById(Long id);

    Long getQuantity();

    List<Long> getOwnerApartmentAccountsIds(Long id);

    String saveOwnerImage(Long id, MultipartFile file1) throws IOException;

    Boolean isHaveDebt(String debt);

    UserStatus stringStatusConverter(String status);

    Long countAllOwners();

    OwnerDTO findOwnerDTObyEmail(String mail);

    OwnerDTO findOwnerDTObyEmailWithMessages(String mail);

    OwnerDTO findOwnerDTObyEmailFull(String mail);

    OwnerDTO convertOwnerToOwnerDTO(Owner owner);
}
