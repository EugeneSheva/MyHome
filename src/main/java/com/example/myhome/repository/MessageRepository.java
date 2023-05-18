package com.example.myhome.repository;

import com.example.myhome.model.Message;
import com.example.myhome.specification.MessageSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long>, JpaSpecificationExecutor<Message> {

//    @Query("SELECT m FROM Message m JOIN m.receivers r WHERE r.id = :ownerId")
//    List<Message> findAllMessagesByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT m FROM Message m JOIN m.receivers r JOIN r.messages rm WHERE rm.id = :ownerId GROUP BY m.id")
    List<Message> findAllMessagesByOwnerId(@Param("ownerId") Long ownerId);

    Page<Message> findAll(Pageable pageable);
    default Page<Message> findByFilters(String text, Long reciverId, Pageable pageable) {
        Specification<Message> spec = Specification.where(null);

        if (reciverId != null && reciverId>0) {
            spec = spec.and(MessageSpecifications.receiverContains(reciverId));
        }
        if (text != null && !text.isEmpty()) {
            spec = spec.and(MessageSpecifications.textContains(text));
        }

        return findAll(spec,pageable);
    }

}
