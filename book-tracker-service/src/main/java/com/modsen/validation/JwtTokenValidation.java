package com.modsen.validation;

import com.modsen.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenValidation {
  private static final String AUTHORITIES = "authorities";

  @Value("${spring.security.secret}")
  private String secret;

  @Value("${spring.security.issuer}")
  private String issuer;

  @SuppressWarnings("unchecked")
  public List<String> getAuthorities(String token) {
    checkToken(token);
    return getClaims(token).get(AUTHORITIES, List.class);
  }

  public String getEmail(String token) {
    checkToken(token);
    return getClaims(token).getSubject();
  }

  private void checkToken(String token) {
    boolean isValid;
    try {
      isValid = !getClaims(token).getExpiration().before(new Date());
    } catch (JwtException exception) {
      isValid = false;
    }
    if (!isValid) {
      throw new InvalidTokenException();
    }
  }

  private Claims getClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSignInKey())
        .requireIssuer(issuer)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  private Key getSignInKey() {
    byte[] decode = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(decode);
  }
}
