package com.modsen.data;

import com.modsen.model.dto.request.RefreshTokenRequest;
import lombok.Builder;

@Builder(setterPrefix = "with")
public class RefreshTokenRequestTestData {
  @Builder.Default private String refreshToken = "refreshToken";

  public RefreshTokenRequest buildRefreshTokenRequest() {
    return new RefreshTokenRequest(refreshToken);
  }
}
