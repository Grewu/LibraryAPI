package com.modsen;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@EnableFeignClients
@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class BookStorageServiceApplication {
  public static void main(String[] args) {
    SpringApplication.run(BookStorageServiceApplication.class, args);
  }
}
