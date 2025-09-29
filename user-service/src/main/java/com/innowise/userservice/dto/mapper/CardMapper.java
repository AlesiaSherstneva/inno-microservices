package com.innowise.userservice.dto.mapper;

import com.innowise.userservice.dto.CardResponseDto;
import com.innowise.userservice.entity.Card;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardMapper {
    CardResponseDto toResponseDto(Card card);

    List<CardResponseDto> toResponseDtoList(List<Card> cards);
}