package com.walmart.pna.api.call.service;

import com.walmart.pna.api.call.model.PNAOfferDTO;
import com.walmart.pna.api.call.model.SkuProdDTO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.IntStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class PnAService {

  @Autowired WebClient webClient;

  @Value("${sku.count.per.request}")
  private int skuCountPerRequest;

  //  @Value("${refresh.cache}")
  //  private boolean refreshCache;

  public void callPnA(MultipartFile skuFile, MultipartFile fsaFile, boolean refreshCache) {
    AtomicLong response_200 = new AtomicLong(0);
    AtomicLong response_400 = new AtomicLong(0);
    AtomicInteger requestCount = new AtomicInteger(0);
    List<String> fsaList = new ArrayList<>();
    List<String> skuList = new ArrayList<>();

    prepareFsaSkuList(skuFile, fsaFile, fsaList, skuList);

    log.info("Started... ");

    long start = System.currentTimeMillis();
    fsaList.forEach(
        fsa -> {
          IntStream.range(0, (skuList.size() + skuCountPerRequest - 1) / skuCountPerRequest)
              .mapToObj(
                  i ->
                      skuList.subList(
                          i * skuCountPerRequest,
                          Math.min(skuList.size(), (i + 1) * skuCountPerRequest)))
              .forEach(
                  batch -> {
                    SkuProdDTO skuProdDTO = new SkuProdDTO();
                    skuProdDTO.setProductId("10134189");
                    skuProdDTO.setSkuIds(batch);
                    List<SkuProdDTO> skuProdDTOList = Collections.singletonList(skuProdDTO);

                    PNAOfferDTO pnaOfferDTO = new PNAOfferDTO(fsa, "", "3048", skuProdDTOList);

                    doPnARestCall2(pnaOfferDTO, false, refreshCache, response_200, response_400);
                    try {
                      // Sleep needed because if the asynchronous handling has higher delays(PnA
                      // service), that means
                      // you can end up with more and more requests coming sending to it, and more
                      // of
                      // those
                      // asynchronous tasks piling up. This will cause PoolAcquireTimeoutException:
                      // Pool#acquire(Duration) has been pending for more than the configured
                      // timeout of
                      // 45000ms
                      Thread.sleep(30);
                    } catch (InterruptedException e) {
                      e.printStackTrace();
                    }
                    requestCount.incrementAndGet();
                  });
        });
    log.info("Time taken in seconds : " + (System.currentTimeMillis() - start) / 1000);
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    log.info("Total requests sent : " + requestCount.get());
    log.info("Total success response received : " + response_200.get());
    log.info("Total error response received : " + response_400.get());
  }

  public void callPnAcallWithOneSku(
      MultipartFile skuFile, MultipartFile fsaFile, boolean refreshCache, boolean metricsEnabled) {

    List<String> fsaList = new ArrayList<>();
    List<String> skuList = new ArrayList<>();

    prepareFsaSkuList(skuFile, fsaFile, fsaList, skuList);
    AtomicInteger requestCount = new AtomicInteger(0);
    AtomicLong response_200 = new AtomicLong(0);
    AtomicLong response_400 = new AtomicLong(0);
    fsaList.forEach(
        fsa -> {
          skuList.forEach(
              sku -> {
                //                log.info("fsaFile:" + fsa + " and skuFile:" + sku);
                SkuProdDTO skuProdDTO = new SkuProdDTO();
                skuProdDTO.setProductId("10134189");
                skuProdDTO.setSkuIds(Collections.singletonList(sku));
                List<SkuProdDTO> skuProdDTOList = Collections.singletonList(skuProdDTO);

                PNAOfferDTO pnaOfferDTO = new PNAOfferDTO(fsa, "", "3048", skuProdDTOList);

                //                doPnARestCall(pnaOfferDTO, metricsEnabled, refreshCache);
                doPnARestCall2(
                    pnaOfferDTO, metricsEnabled, refreshCache, response_200, response_400);
                try {
                  // Sleep needed because if the asynchronous handling has higher delays(PnA
                  // service), that means
                  // you can end up with more and more requests coming sending to it, and more of
                  // those
                  // asynchronous tasks piling up. This will cause PoolAcquireTimeoutException:
                  // Pool#acquire(Duration) has been pending for more than the configured timeout of
                  // 45000ms
                  Thread.sleep(10);
                } catch (InterruptedException e) {
                  e.printStackTrace();
                }
                requestCount.incrementAndGet();
              });
        });
    System.out.println(requestCount.get());
  }

  private void prepareFsaSkuList(
      MultipartFile sku, MultipartFile fsa, List<String> fsaList, List<String> skuList) {

    BufferedReader br1;
    BufferedReader br2;
    try {
      String skuId;
      InputStream skuIs = sku.getInputStream();
      br1 = new BufferedReader(new InputStreamReader(skuIs, StandardCharsets.UTF_8));
      while ((skuId = br1.readLine()) != null) {
        skuList.add(skuId);
      }

      String fsaId;
      InputStream fsaIs = fsa.getInputStream();
      br2 = new BufferedReader(new InputStreamReader(fsaIs, StandardCharsets.UTF_8));
      while ((fsaId = br2.readLine()) != null) {
        fsaList.add(fsaId);
      }
      br1.close();
      br2.close();
    } catch (IOException e) {
      System.err.println(e.getMessage());
    }
  }

  private void doPnARestCall(
      PNAOfferDTO pnaOfferDTO, boolean metricsEnabled, boolean refreshCache) {
    webClient
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
        .body(Mono.just(pnaOfferDTO), PNAOfferDTO.class)
        .exchangeToFlux(sa -> sa.bodyToFlux(String.class))
        .subscribe(System.out::println);

    //    .retrieve().bodyToMono(String.class).block();

  }

  private void doPnARestCall2(
      PNAOfferDTO pnaOfferDTO,
      boolean metricsEnabled,
      boolean refreshCache,
      AtomicLong response_success,
      AtomicLong response_error) {
    webClient
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
        .body(Mono.just(pnaOfferDTO), PNAOfferDTO.class)
        .retrieve()
        .onStatus(
            HttpStatus::isError,
            clientResponse -> {
              log.error("Status code - {} and error.", clientResponse.statusCode());
              return Mono.empty();
            })
        .onStatus(
            HttpStatus::is2xxSuccessful,
            clientResponse -> {
              response_success.incrementAndGet();
              //              log.info(
              //                  "Status code - {} and request count is - {}",
              //                  clientResponse.statusCode(),
              //                  response_success.incrementAndGet());
              return Mono.empty();
            })
        .bodyToMono(String.class)
        .timeout(Duration.ofMillis(50000))
        // detecting the timeout error
        .doOnError(
            error -> {
              System.out.println("error received...");
              log.error("Error signal detected", error);
              response_error.incrementAndGet();
            })
        .subscribe();
  }
}
