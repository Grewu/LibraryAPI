package com.modsen.controller;

import com.modsen.model.dto.request.UserRequest;
import com.modsen.model.dto.response.UserResponse;
import com.modsen.service.api.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = UserRestController.USER_API_PATH)
public class UserRestController {

  protected static final String USER_API_PATH = "/api/v0/users";
  private final UserService userService;

  @PostMapping
  @PreAuthorize("hasAuthority('user:write')")
  public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest userRequest) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .contentType(MediaType.APPLICATION_JSON)
        .body(userService.create(userRequest));
  }

  @GetMapping
  @PreAuthorize("hasAuthority('user:read')")
  public ResponseEntity<Page<UserResponse>> getAll(@PageableDefault(20) Pageable pageable) {
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(userService.getAll(pageable));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('user:read')")
  public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(userService.getById(id));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('user:write')")
  public ResponseEntity<UserResponse> update(
      @PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
    return ResponseEntity.status(HttpStatus.OK)
        .contentType(MediaType.APPLICATION_JSON)
        .body(userService.update(id, userRequest));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('user:delete')")
  public ResponseEntity<Void> delete(@PathVariable Long id) {
    userService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
