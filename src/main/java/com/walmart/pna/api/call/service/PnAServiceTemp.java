package com.walmart.pna.api.call.service;

import com.walmart.pna.api.call.model.PNAOfferDTO;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class PnAServiceTemp {

  @Autowired WebClient webClient;

  public void callPnAapi(PNAOfferDTO pnaOfferDTO) {

    webClient
        .post()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/v2/product/offerDetails")
                    .queryParam("includeAllOffers", true)
                    .queryParam("metricsEnabled", true)
                    .queryParam("refreshCache", true)
                    .build())
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(pnaOfferDTO), PNAOfferDTO.class)
        //        .exchangeToFlux(sa -> sa.bodyToFlux(String.class))
        .retrieve()
        .onStatus(
            HttpStatus::isError,
            clientResponse -> {
              log.error(
                  "Status code - {} and error is - {}",
                  clientResponse.statusCode(),
                  clientResponse.bodyToMono(String.class));
              throw new RuntimeException("Error occured");
            })
        .bodyToMono(String.class)
        .timeout(Duration.ofMillis(50000))
        // detecting the timeout error
        .doOnError(error -> log.error("Error signal detected", error))
        .subscribe(log::info);
  }

  public Flux<String> callPnAWithRequestBody(
      PNAOfferDTO pnaRequest, boolean refreshCache, boolean metricsEnabled) {
    return webClient
        .post()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/v2/product/offerDetails")
                    .queryParam("includeAllOffers", true)
                    .queryParam("metricsEnabled", metricsEnabled)
                    .queryParam("refreshCache", refreshCache)
                    .build())
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(pnaRequest), PNAOfferDTO.class)
        .retrieve()
        .bodyToFlux(String.class);
    //        .block();
  }

  public void pnAWithErrorHandling(PNAOfferDTO pnaOfferDTO) {
    webClient
        .post()
        .uri(
            uriBuilder ->
                uriBuilder
                    .path("/v2/product/offerDetails")
                    .queryParam("includeAllOffers", true)
                    .queryParam("metricsEnabled", true)
                    .queryParam("refreshCache", true)
                    .build())
        .accept(MediaType.APPLICATION_JSON)
        .body(Mono.just(pnaOfferDTO), PNAOfferDTO.class)
        .exchangeToFlux(
            sa -> {
              if (sa.statusCode().is4xxClientError() || sa.statusCode().is5xxServerError()) {
                log.error(
                    "Client/Server Error while connecting to endpoint code {} ", sa.statusCode());
                return Flux.error(new RuntimeException("error"));
              }
              return sa.bodyToFlux(String.class);
            })
        .subscribe(log::info);
    //        .retrieve()
    //        .bodyToFlux(String.class);
    //        .block();
  }
}
