//package com.walmart.pna.api.call.service;
//
//import com.walmart.pna.api.call.model.PNAOfferDTO;
//import com.walmart.pna.api.call.model.SkuProdDTO;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.nio.charset.StandardCharsets;
//import java.time.Duration;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.util.concurrent.atomic.AtomicLong;
//import java.util.stream.IntStream;
//import javax.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.Resource;
//import org.springframework.core.io.ResourceLoader;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//@Service
//@Slf4j
//public class PnACallScheduledService {
//
//  @Autowired ResourceLoader resourceLoader1;
//  @Autowired ResourceLoader resourceLoader2;
//  @Autowired WebClient webClient;
//
//  @Value("${sku.count.per.request}")
//  private int skuCountPerRequest;
//
//  @Value("${refresh.cache}")
//  private boolean refreshCache;
//
//  List<String> fsaList = new ArrayList<>(100);
//  List<String> skuList = new ArrayList<>(7000);
//
//  //    @Scheduled(fixedRate = 2000)
//  //    public void printLine(){
//  //      System.out.println("scheduled task run::");
//  //      log.info("hello task");
//  //    }
//
//  @PostConstruct
//  public void dataSetup() {
//    try {
//      Thread.sleep(10000);
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
//    Resource fsaResource = resourceLoader1.getResource("classpath:fsas.txt");
//
//    try (InputStream inputStream = fsaResource.getInputStream();
//        BufferedReader br1 =
//            new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
//
//      String fsaId;
//      while ((fsaId = br1.readLine()) != null) {
//        fsaList.add(fsaId);
//      }
//      //      fsaList.forEach(System.out::println);
//    } catch (IOException e) {
//      log.error("IOException", e);
//    }
//
//    Resource skuResource = resourceLoader2.getResource("classpath:skus.txt");
//
//    try (InputStream inputStream2 = skuResource.getInputStream();
//        BufferedReader br2 =
//            new BufferedReader(new InputStreamReader(inputStream2, StandardCharsets.UTF_8))) {
//      String skuId;
//      while ((skuId = br2.readLine()) != null) {
//        skuList.add(skuId);
//      }
//      //      skuList.forEach(System.out::println);
//
//    } catch (IOException e) {
//      log.error("IOException", e);
//    }
//  }
//
//  @Scheduled(fixedRate = 60000, initialDelay = 5000)
//  public void callPnA() {
//    AtomicLong response_200 = new AtomicLong(0);
//    AtomicLong response_400 = new AtomicLong(0);
//    AtomicInteger requestCount = new AtomicInteger(0);
//    long start = System.currentTimeMillis();
//    fsaList.forEach(
//        fsa -> {
//          IntStream.range(0, (skuList.size() + skuCountPerRequest - 1) / skuCountPerRequest)
//              .mapToObj(
//                  i ->
//                      skuList.subList(
//                          i * skuCountPerRequest,
//                          Math.min(skuList.size(), (i + 1) * skuCountPerRequest)))
//              .forEach(
//                  batch -> {
//                    SkuProdDTO skuProdDTO = new SkuProdDTO();
//                    skuProdDTO.setProductId("10134189");
//                    skuProdDTO.setSkuIds(batch);
//                    List<SkuProdDTO> skuProdDTOList = Collections.singletonList(skuProdDTO);
//
//                    PNAOfferDTO pnaOfferDTO = new PNAOfferDTO(fsa, "", "3048", skuProdDTOList);
//
//                    doPnARestCall2(pnaOfferDTO, refreshCache, response_200, response_400);
//                    try {
//                      // Sleep needed because if the asynchronous handling has higher delays(PnA
//                      // service), that means
//                      // you can end up with more and more requests coming sending to it, and more
//                      // of
//                      // those
//                      // asynchronous tasks piling up. This will cause PoolAcquireTimeoutException:
//                      // Pool#acquire(Duration) has been pending for more than the configured
//                      // timeout of
//                      // 45000ms
//                      Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                      e.printStackTrace();
//                    }
//                    requestCount.incrementAndGet();
//                  });
//        });
//    log.info("total requests sent : " + requestCount.get());
//    log.info("Time taken in seconds : " + (System.currentTimeMillis() - start)/1000);
//  }
//
//  private void doPnARestCall2(
//      PNAOfferDTO pnaOfferDTO,
//      boolean refreshCache,
//      AtomicLong response_success,
//      AtomicLong response_error) {
//    webClient
//        .post()
//        .uri(
//            uriBuilder ->
//                uriBuilder
//                    .path("/v2/product/offerDetails")
//                    .queryParam("includeAllOffers", true)
//                    .queryParam("refreshCache", refreshCache)
//                    .build())
//        .accept(MediaType.APPLICATION_JSON)
//        .body(Mono.just(pnaOfferDTO), PNAOfferDTO.class)
//        .retrieve()
//        .onStatus(
//            HttpStatus::isError,
//            clientResponse -> {
//              log.error(
//                  "Status code - {} and error count is - {}",
//                  clientResponse.statusCode(),
//                  response_error.incrementAndGet());
//              throw new RuntimeException("Error occured");
//            })
//        .onStatus(
//            HttpStatus::is2xxSuccessful,
//            clientResponse -> {
//              log.info(
//                  "Success response received count is - {} ", response_success.incrementAndGet());
//              return Mono.empty();
//            })
//        .bodyToMono(String.class)
//        .timeout(Duration.ofMillis(55000))
//        // detecting the timeout error
//        .doOnError(
//            error -> {
//              System.out.println("error received...");
//              log.error("Error signal detected", error);
//            })
//        .subscribe();
//  }
//}
