package com.walmart.pna.api.call.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/** @author n0s011l */
@Configuration
// @EnableSwagger2
public class SwaggerConfig {
  /*
   * http://localhost:8080/v2/api-docs
   * http://localhost:8080/swagger-ui/index.html
   *
   */
  @Bean
  public Docket swaggerConfiguration() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.basePackage("com.walmart"))
        .paths(PathSelectors.any())
        //  .paths(regex("/product*"))
        //  .apis(RequestHandlerSelectors.any())
        //  .paths(PathSelectors.ant("/v2/*"))
        .build()
        .apiInfo(metaData());
  }

  private ApiInfo metaData() {
    return new ApiInfoBuilder()
        .title("PnO REST API")
        .version("1.0.0")
        .license("Walmart")
        //  .description("PnA rest call")
        //  .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0\"")
        .contact(
            new Contact(
                "CA-IDC-PO-ENGG",
                "https://confluence.walmart.com/pages/viewpage.action?spaceKey=CAENGR&title=PnO+Engineering+Cookbook",
                "CA-IDC-PO-ENGG@email.wal-mart.com"))
        .build();
  }
}
