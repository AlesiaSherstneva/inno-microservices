package com.innowise.userservice.repository;

import com.innowise.userservice.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    @Query(value = "SELECT * FROM card_info WHERE id IN :ids", nativeQuery = true)
    List<Card> findCardsByIdIn(@Param("ids") List<Long> ids);

    boolean existsByNumber(String number);
}