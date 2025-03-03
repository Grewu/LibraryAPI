package com.modsen.controller.filter;

import com.modsen.validation.JwtTokenValidation;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * A filter for processing JSON Web Tokens (JWT) in incoming requests.
 *
 * <p>The {@code JwtFilter} intercepts HTTP requests to extract and validate JWTs. It authenticates
 * users based on the token and sets the authentication context for the current request.
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
  private static final int BEGIN_INDEX = 7;
  private static final String TYPE_AUTHORIZATION = "Bearer ";
  private final JwtTokenValidation jwtTokenValidation;

  /**
   * Filters incoming requests to extract and validate the JWT from the Authorization header.
   *
   * @param req the HttpServletRequest containing the incoming request
   * @param resp the HttpServletResponse to write the response to
   * @param filterChain the FilterChain for continuing the request processing
   * @throws ServletException if the request could not be handled
   * @throws IOException if an input or output error occurs
   */
  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest req,
      @NonNull HttpServletResponse resp,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    var header = req.getHeader(HttpHeaders.AUTHORIZATION);
    if (Objects.isNull(header) || !header.startsWith(TYPE_AUTHORIZATION)) {
      filterChain.doFilter(req, resp);
      return;
    }

    var token = header.substring(BEGIN_INDEX);

    var principal = jwtTokenValidation.getEmail(token);

    var grantedAuthorities =
        jwtTokenValidation.getAuthorities(token).stream().map(SimpleGrantedAuthority::new).toList();

    var authenticated =
        UsernamePasswordAuthenticationToken.authenticated(principal, null, grantedAuthorities);
    authenticated.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

    SecurityContextHolder.getContext().setAuthentication(authenticated);

    filterChain.doFilter(req, resp);
  }
}
