package com.modsen.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.modsen.data.UserTestData;
import com.modsen.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ContextConfiguration(classes = JwtTokenServiceImpl.class)
@TestPropertySource(
    properties = {
      "spring.security.secret=244226452948404D6351655468576D5A7134743777217A25432A462D4A614E645267556A586E"
          + "3272357538782F413F4428472B4B6250655368566D5970337336d",
      "spring.security.issuer=test"
    })
class JwtTokenServiceImplTest {

  @Value("${spring.security.secret}")
  private String secret;

  @Value("${spring.security.issuer}")
  private String issuer;

  @Autowired private JwtTokenServiceImpl tokenService;

  @MockitoBean private UserRepository userRepository;

  @MockitoBean private UserServiceImpl userService;

  @Test
  void generateTokenShouldReturnValidToken() {
    // given
    var user = UserTestData.builder().build().buildUser();

    // when
    var token = tokenService.generateAccessToken(user);
    var decode = Decoders.BASE64.decode(secret);

    var claims =
        Jwts.parserBuilder()
            .requireIssuer(issuer)
            .setSigningKey(Keys.hmacShaKeyFor(decode))
            .build()
            .parseClaimsJws(token)
            .getBody();

    // then
    assertTrue(claims.getExpiration().after(new Date()));
    assertEquals(user.getEmail(), claims.getSubject());
    assertEquals(issuer, claims.getIssuer());
    assertNotNull(claims.getIssuedAt());
    assertTrue(claims.getIssuedAt().before(new Date()));
  }

  @Test
  void getUserNameShouldReturnCorrectUserName() {
    // given
    var user = UserTestData.builder().build().buildUser();

    // when
    var token = tokenService.generateRefreshToken(user);
    var email = tokenService.getEmail(token);

    // then
    assertEquals(user.getEmail(), email);
  }

  @Test
  void shouldReturnUpdateRefreshToken() {
    var user = UserTestData.builder().build().buildUser();
    var token = tokenService.generateRefreshToken(user);
    var email = user.getEmail();

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

    var refreshToken = tokenService.updateRefreshToken(token);

    assertNotNull(refreshToken);
    assertEquals(user.getEmail(), email);
    verify(userRepository).findByEmail(email);
  }

  @Test
  void shouldReturnRefreshAccessToken() {
    var user = UserTestData.builder().build().buildUser();
    var token = tokenService.generateAccessToken(user);
    var email = user.getEmail();

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

    var refreshToken = tokenService.refreshAccessToken(token);

    assertNotNull(refreshToken);
    assertEquals(user.getEmail(), email);
    verify(userRepository).findByEmail(email);
  }
}
