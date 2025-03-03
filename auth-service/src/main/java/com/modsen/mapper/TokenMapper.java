package com.modsen.mapper;

import com.modsen.model.dto.response.TokenResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TokenMapper {
  @Mapping(target = "accessToken", source = "accessToken")
  @Mapping(target = "refreshToken", source = "refreshToken")
  TokenResponse toTokenResponse(String accessToken, String refreshToken);
}
