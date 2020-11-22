package com.walmart.pna.api.call.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

@Configuration
public class BeanInitialization {

  @Bean
  public WebClient getWebClient() {

    //    WebClient client = WebClient.builder()
    //        .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)))
    //        .build();

//    TcpClient tcpClient =
//        TcpClient.create()
//            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
//            .doOnConnected(
//                connection ->
//                    connection
//                        .addHandlerLast(new ReadTimeoutHandler(10000))
//                        .addHandlerLast(new WriteTimeoutHandler(10000))
//                        .addHandlerLast(new LoggingHandler(LogLevel.TRACE)));

    return WebClient.builder()
        .baseUrl("http://ca-priceoffersrvs-prod.walmart.com/ca-priceoffersrvcs-app")
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();
  }
}
