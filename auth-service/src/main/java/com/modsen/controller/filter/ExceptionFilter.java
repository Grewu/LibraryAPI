package com.modsen.controller.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.exception.AbstractExceptionMessageException;
import com.modsen.exception.ExceptionMessage;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class ExceptionFilter extends OncePerRequestFilter {

  private final ObjectMapper mapper;

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      FilterChain filterChain)
      throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } catch (AbstractExceptionMessageException e) {
      sendException(response, e.getExceptionMessage());
    }
  }

  public void sendException(HttpServletResponse response, AuthenticationException e)
      throws IOException {
    sendException(response, new ExceptionMessage(HttpStatus.FORBIDDEN, e.getMessage()));
  }

  private void sendException(HttpServletResponse response, ExceptionMessage exceptionMessage)
      throws IOException {
    response.setStatus(exceptionMessage.status().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(mapper.writeValueAsString(exceptionMessage));
  }
}
