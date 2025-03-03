package com.modsen.service.impl;

import com.modsen.exception.EntityNotFoundException;
import com.modsen.exception.InvalidTokenException;
import com.modsen.model.entity.User;
import com.modsen.repository.UserRepository;
import com.modsen.service.api.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

/**
 * The JwtTokenServiceImpl class implements the TokenService interface to provide functionality for
 * generating and validating JSON Web Tokens (JWT).
 */
@Service
@RequiredArgsConstructor
public class JwtTokenServiceImpl implements TokenService {

  public static final String AUTHORITIES = "authorities";

  @Value("${spring.security.secret}")
  private String secret;

  @Value("${spring.security.issuer}")
  private String issuer;

  @Value("${spring.security.access}")
  private long access;

  @Value("${spring.security.refresh}")
  private long refresh;

  private final UserRepository userRepository;

  /**
   * Generates a JWT for the given user.
   *
   * @param user the user for whom the token is generated
   * @return a signed JWT as a string
   */
  @Override
  public String generateAccessToken(User user) {
    var authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
    return Jwts.builder()
        .setSubject(user.getEmail())
        .setIssuer(issuer)
        .claim(AUTHORITIES, authorities)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + access))
        .signWith(getSignInKey(), SignatureAlgorithm.HS512)
        .compact();
  }

  /**
   * Generates a JWT for the given user.
   *
   * @param user the user for whom the token is generated
   * @return a signed JWT as a string
   */
  @Override
  public String generateRefreshToken(User user) {
    return Jwts.builder()
        .setSubject(user.getEmail())
        .setIssuer(issuer)
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + refresh))
        .signWith(getSignInKey(), SignatureAlgorithm.HS512)
        .compact();
  }

  @Override
  public String updateRefreshToken(String refreshToken) {
    var user = validateTokenAndGetUserByEmail(refreshToken);
    return generateRefreshToken(user);
  }

  @Override
  public String refreshAccessToken(String refreshToken) {
    var user = validateTokenAndGetUserByEmail(refreshToken);
    return generateAccessToken(user);
  }

  private User validateTokenAndGetUserByEmail(String refreshToken) {
    if (!validateToken(refreshToken)) {
      throw new InvalidTokenException();
    }
    var email = getEmail(refreshToken);
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new EntityNotFoundException(User.class, email));
  }

  /**
   * Retrieves the email from the given JWT.
   *
   * @param token the JWT to extract the email from
   * @return the email contained in the JWT
   * @throws InvalidTokenException if the token is invalid or expired
   */
  @Override
  public String getEmail(String token) {
    checkToken(token);
    return getClaims(token).getSubject();
  }

  /**
   * Validates the provided JWT token, checking its expiration.
   *
   * @param token the JWT to validate
   * @throws InvalidTokenException if the token is expired or invalid
   */
  private void checkToken(String token) {
    boolean isValid;
    try {
      isValid = validateToken(token);
    } catch (JwtException exception) {
      isValid = false;
    }
    if (!isValid) {
      throw new InvalidTokenException();
    }
  }

  private boolean validateToken(String token) {
    return !getClaims(token).getExpiration().before(new Date());
  }

  /**
   * Parses the claims from the provided JWT.
   *
   * @param token the JWT to parse claims from
   * @return the claims contained in the JWT
   */
  private Claims getClaims(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(getSignInKey())
        .requireIssuer(issuer)
        .build()
        .parseClaimsJws(token)
        .getBody();
  }

  /**
   * Retrieves the signing key from the base64-encoded secret.
   *
   * @return the signing key used for token signing
   */
  private Key getSignInKey() {
    byte[] decode = Decoders.BASE64.decode(secret);
    return Keys.hmacShaKeyFor(decode);
  }
}
