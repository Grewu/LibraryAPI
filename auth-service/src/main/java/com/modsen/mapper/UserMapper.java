package com.modsen.mapper;

import com.modsen.model.dto.request.UserRequest;
import com.modsen.model.dto.response.UserResponse;
import com.modsen.model.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

/**
 * Mapper interface for converting between {@link User} entities and their corresponding DTOs,
 * {@link UserRequest} and {@link UserResponse}.
 *
 * <p>This interface utilizes MapStruct to generate the implementation for mapping properties
 * between the entities and DTOs. It is annotated with {@code @Mapper(componentModel = "spring",
 * unmappedTargetPolicy = ReportingPolicy.IGNORE)} to enable Spring's component scanning and ignore
 * any unmapped target properties.
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {

  /**
   * Converts a {@link UserRequest} to a {@link User} entity.
   *
   * @param userRequest the DTO containing the details of the user
   * @return the converted {@link User} entity
   */
  @Mapping(target = User.Fields.id, ignore = true)
  @Mapping(target = User.Fields.role, expression = "java(new Role(userRequest.role()))")
  User toUser(UserRequest userRequest);

  /**
   * Converts a {@link User} entity to a {@link UserResponse} DTO.
   *
   * @param user the user entity to be converted
   * @return the converted {@link UserResponse} DTO
   */
  @Mapping(target = User.Fields.role, source = "role.id")
  UserResponse toUserResponse(User user);

  @Mapping(target = User.Fields.id, ignore = true)
  @Mapping(target = User.Fields.role, expression = "java(new Role(userRequest.role()))")
  User update(UserRequest userRequest, @MappingTarget User current);
}
