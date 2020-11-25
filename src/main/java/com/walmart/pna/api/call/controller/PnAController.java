package com.walmart.pna.api.call.controller;

import com.walmart.pna.api.call.service.PnAService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/v2")
@Api(tags = "PnA Controller")
public class PnAController {

  @Autowired PnAService pnAService;
  //  @Autowired
  //  PnACallScheduledService pnACallScheduledService;

  @PostMapping("/product/offerDetails")
  public void callPnAWithFsaAndSkuFile(
      //      @RequestParam("file") MultipartFile file,
      @RequestPart(required = true) MultipartFile sku,
      @RequestPart(required = true) MultipartFile fsa,
      @RequestParam(value = "refreshCache", required = false, defaultValue = "false")
          boolean refreshCache) {

    pnAService.callPnA(sku, fsa, refreshCache);
  }
  //
  //  @GetMapping("/test/file")
  //  public void test1() {
  //    pnACallScheduledService.dataSetup();
  //    pnACallScheduledService.callPnA();
  //  }
}
