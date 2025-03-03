package com.modsen.controller.filter;

import com.modsen.service.api.TokenService;
import com.modsen.service.api.UserService;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private static final String TYPE_AUTHORIZATION = "Bearer ";
  public static final int BEGIN_INDEX = 7;

  private final TokenService tokenService;
  private final UserService userService;

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
    var userName = tokenService.getEmail(token);
    var userDetails = userService.loadUserByUsername(userName);

    var authenticated =
        UsernamePasswordAuthenticationToken.authenticated(
            userDetails, null, userDetails.getAuthorities());
    authenticated.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));

    SecurityContextHolder.getContext().setAuthentication(authenticated);

    filterChain.doFilter(req, resp);
  }
}
