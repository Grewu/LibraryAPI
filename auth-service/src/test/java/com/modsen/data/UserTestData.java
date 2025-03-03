package com.modsen.data;

import com.modsen.model.dto.request.UserRequest;
import com.modsen.model.dto.response.UserResponse;
import com.modsen.model.entity.Role;
import com.modsen.model.entity.User;
import com.modsen.model.entity.enums.RoleType;
import lombok.Builder;

@Builder(setterPrefix = "with")
public class UserTestData {

  @Builder.Default private Long id = 1L;

  @Builder.Default private String email = "user@example.com";

  @Builder.Default private String password = "password";

  @Builder.Default private Role role = new Role(1L, RoleType.USER);

  public User buildUser() {
    return new User(id, email, password, role);
  }

  public UserRequest buildUserRequest() {
    return new UserRequest(email, password, role.getId());
  }

  public UserResponse buildUserResponse() {
    return new UserResponse(id, email, role.getId());
  }
}
