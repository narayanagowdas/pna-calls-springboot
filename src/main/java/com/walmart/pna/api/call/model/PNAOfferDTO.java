package com.walmart.pna.api.call.model;

import com.google.gson.Gson;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * @author n0s011l
 */
public class PNAOfferDTO {
  @NotNull
  @Pattern(regexp = "^[a-zA-Z]\\d[a-zA-Z]$")
  @Max(value = 3, message = "FSA should be 3 a character length")
  @Min(value = 3, message = "FSA should be 3 a character length")
  private String fsa;

  @NotNull
  @Valid
  @NotBlank
  private String availabilityStoreId;

  @Valid
  private String pricingStoreId;

  @NotNull
  @Valid
  private List<SkuProdDTO> products;

  public PNAOfferDTO(String fsa, String pricingStoreId, String availabilityStoreId, List<SkuProdDTO> products) {
    super();
    this.fsa = fsa;
    this.pricingStoreId = pricingStoreId;
    this.availabilityStoreId = availabilityStoreId;
    this.products = products;
  }

  /**
   * This is the default constructor
   */
  public PNAOfferDTO() {
    super();
  }

  @ApiModelProperty(value = "FSA from the postal code = [Pattern='^[a-zA-Z]\\d[a-zA-Z]$']", required = true, position = 0, example = "A1A")
  public String getFsa() {
    return fsa;
  }

  public void setFsa(String fsa) {
    this.fsa = fsa;
  }

  @ApiModelProperty(value = "Each key is a product ID, with value being an array of selected SKU IDs within that product", required = true, position = 2)
  public List<SkuProdDTO> getProducts() {
    return products;
  }

  public void setProducts(List<SkuProdDTO> products) {
    this.products = products;
  }

  @ApiModelProperty(value = "ID of the Store where slot is booked for Grocery Pickup/Delivery", required = true, position = 1, example = "3095")
  public String getAvailabilityStoreId() {
    return availabilityStoreId;
  }

  public void setAvailabilityStoreId(String availabilityStoreId) {
    this.availabilityStoreId = availabilityStoreId;
  }

  @ApiModelProperty(value = "ID of the Store for where local store price have to be used", required = true, position = 2, example = "3095")
  public String getPricingStoreId() {
    return pricingStoreId;
  }

  public void setPricingStoreId(String pricingStoreId) {
    this.pricingStoreId = pricingStoreId;
  }

  @Override
  public String toString() {
    return new Gson().toJson(this);
  }
}
