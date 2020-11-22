package com.walmart.pna.api.call.controller;

import com.walmart.pna.api.call.model.PNAOfferDTO;
import com.walmart.pna.api.call.model.SkuProdDTO;
import com.walmart.pna.api.call.service.PnAServiceTemp;
import io.swagger.annotations.Api;
import java.util.Collections;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/v2")
@Api(tags = "PnA Controller Test")
public class PnAControllerTemp {

  @Autowired WebClient webClient;
  @Autowired PnAServiceTemp pnAServiceTemp;

  @PostMapping("/product/offerDetails/test1")
  public void callPnATest1() {

    SkuProdDTO skuProdDTO = new SkuProdDTO();
    skuProdDTO.setProductId("10134189");
    skuProdDTO.setSkuIds(Collections.singletonList("6000197873223aaa"));
    List<SkuProdDTO> skuProdDTOList = Collections.singletonList(skuProdDTO);

    PNAOfferDTO pnaOfferDTO = new PNAOfferDTO("K1C", "", "3048", skuProdDTOList);
    pnAServiceTemp.callPnAapi(pnaOfferDTO);
  }

  @PostMapping("/product/offerDetails1")
  public Flux<String> callPnAWithRequestBody(
      //  public String callPnAWithRequestBody(
      @RequestBody() @Valid PNAOfferDTO pnaRequest,
      @RequestParam(value = "refreshCache", required = false, defaultValue = "false")
          boolean refreshCache,
      @RequestParam(value = "metricsEnabled", required = false, defaultValue = "false")
          boolean metricsEnabled) {

    return pnAServiceTemp.callPnAWithRequestBody(pnaRequest, refreshCache, metricsEnabled);
  }

  @PostMapping("/product/offerDetails2")
  public void pnAWithErrorHandling() {

    SkuProdDTO skuProdDTO = new SkuProdDTO();
    skuProdDTO.setProductId("10134189");
    skuProdDTO.setSkuIds(Collections.singletonList("6000197873223"));
    List<SkuProdDTO> skuProdDTOList = Collections.singletonList(skuProdDTO);

    PNAOfferDTO pnaOfferDTO = new PNAOfferDTO("K1C", "", "3048", skuProdDTOList);

    pnAServiceTemp.pnAWithErrorHandling(pnaOfferDTO);
  }
}
