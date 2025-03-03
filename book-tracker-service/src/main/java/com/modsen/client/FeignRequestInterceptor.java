package com.modsen.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignRequestInterceptor implements RequestInterceptor {
  private static final String TYPE_AUTHORIZATION = "Bearer ";

  @Override
  public void apply(RequestTemplate requestTemplate) {
    var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    var authorizationHeader = attributes.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
    if (authorizationHeader != null && authorizationHeader.startsWith(TYPE_AUTHORIZATION)) {
      var token = authorizationHeader.substring(TYPE_AUTHORIZATION.length());
      requestTemplate.header(HttpHeaders.AUTHORIZATION, TYPE_AUTHORIZATION + token);
    }
  }
}
