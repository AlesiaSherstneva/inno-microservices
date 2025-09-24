package com.innowise.userservice.dto.mapper;

import com.innowise.userservice.dto.CardInfoResponseDto;
import com.innowise.userservice.entity.CardInfo;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CardInfoMapper {
    CardInfoResponseDto toResponseDto(CardInfo cardInfo);

    List<CardInfoResponseDto> toResponseDtoList(List<CardInfo> cardInfos);
}