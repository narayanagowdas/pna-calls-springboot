package com.walmart.pna.api.call;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PnaApiCallApplication {

  public static void main(String[] args) {
    SpringApplication.run(PnaApiCallApplication.class, args);
  }
}
