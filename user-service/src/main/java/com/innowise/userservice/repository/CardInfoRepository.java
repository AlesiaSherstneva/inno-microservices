package com.innowise.userservice.repository;

import com.innowise.userservice.entity.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {
    @Query(value = "SELECT * FROM card_info WHERE id IN :ids", nativeQuery = true)
    List<CardInfo> findCardInfosByIdIn(@Param("ids") List<Long> ids);
}