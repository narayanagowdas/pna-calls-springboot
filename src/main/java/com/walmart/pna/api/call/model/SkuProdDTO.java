package com.walmart.pna.api.call.model;

import com.google.gson.Gson;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * @author n0s011l
 */
public class SkuProdDTO {
  @NotNull
  private String productId;

  @NotNull
  @Valid
  private List<String> skuIds;

  public SkuProdDTO(){
    super();
  }


  public SkuProdDTO(String productId, List<String> skuIds) {
    super();
    this.productId = productId;
    this.skuIds = skuIds;
  }

  @ApiModelProperty(value = "ID of the product", required=true, example = "6000196471110")
  public String getProductId() {
    return productId;
  }

  public void setProductId(String productId) {
    this.productId = productId;
  }

  @ApiModelProperty(value = "List of SKUs must be non-empty to allow for sensible rollup attributes", required=true)
  public List<String> getSkuIds() {
    return skuIds;
  }

  public void setSkuIds(List<String> skuIds) {
    this.skuIds = skuIds;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
}
