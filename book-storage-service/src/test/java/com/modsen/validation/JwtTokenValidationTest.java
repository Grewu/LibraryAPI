package com.modsen.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import java.util.LinkedList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
@ContextConfiguration(classes = JwtTokenValidation.class)
@TestPropertySource(
    properties = {
      "spring.security.secret=244226452948404D6351655468576D5A7134743777217A25432A462D4A614E645267556A586E"
          + "3272357538782F413F4428472B4B6250655368566D5970337336d",
      "spring.security.issuer=test"
    })
class JwtTokenValidationTest {
  @MockitoBean private JwtTokenValidation jwtTokenValidation;

  @Test
  void getAuthoritiesShouldReturnEmptyList() {
    // given
    var token = "token";
    var expected = new LinkedList<String>();
    // when
    var actual = jwtTokenValidation.getAuthorities(token);

    // then
    assertEquals(expected, actual);
    verify(jwtTokenValidation).getAuthorities(token);
  }

  @Test
  void shouldReturnNullWhenTokenDoesNotContainEmail() {
    // given
    var token = "token";
    String expected = null;
    // when
    var actual = jwtTokenValidation.getEmail(token);
    // then
    Assertions.assertNull(expected, actual);
  }
}
