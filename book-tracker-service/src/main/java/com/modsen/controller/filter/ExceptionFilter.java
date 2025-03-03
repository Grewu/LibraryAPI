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

/**
 * A filter for handling exceptions in the application.
 *
 * <p>The {@code ExceptionFilter} intercepts requests and captures exceptions that occur during the
 * request processing. It specifically handles exceptions extending {@link
 * AbstractExceptionMessageException} and formats the response accordingly.
 */
@Component
@RequiredArgsConstructor
public class ExceptionFilter extends OncePerRequestFilter {

  private final ObjectMapper mapper;

  /**
   * Filters requests and captures exceptions that occur during request processing.
   *
   * @param request the HttpServletRequest
   * @param response the HttpServletResponse
   * @param filterChain the FilterChain for continuing the request processing
   * @throws ServletException if the request could not be handled
   * @throws IOException if an input or output error occurs
   */
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

  /**
   * Sends an exception response when an {@link AuthenticationException} occurs.
   *
   * @param response the HttpServletResponse to write the error message to
   * @param e the AuthenticationException that was thrown
   * @throws IOException if an input or output error occurs
   */
  public void sendException(HttpServletResponse response, AuthenticationException e)
      throws IOException {
    sendException(response, new ExceptionMessage(HttpStatus.FORBIDDEN, e.getMessage()));
  }

  /**
   * Sends a formatted exception response based on the provided {@link ExceptionMessage}.
   *
   * @param response the HttpServletResponse to write the error message to
   * @param exceptionMessage the ExceptionMessage containing the status and error message
   * @throws IOException if an input or output error occurs
   */
  private void sendException(HttpServletResponse response, ExceptionMessage exceptionMessage)
      throws IOException {
    response.setStatus(exceptionMessage.status().value());
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.getWriter().write(mapper.writeValueAsString(exceptionMessage));
  }
}
