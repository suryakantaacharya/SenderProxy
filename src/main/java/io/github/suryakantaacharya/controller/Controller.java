package io.github.suryakantaacharya.controller;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

@RestController(value = "/")
public class Controller {
    static WebClient webClientBuilderWithProxy(){

        HttpClient httpClient = HttpClient.create()
                .tcpConfiguration(tcpClient ->
                        tcpClient.proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP)
                                .host("103.228.246.36")
                                .port(8080)
                        )
                );

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .exchangeStrategies(ExchangeStrategies.builder().codecs(configurer ->
                                configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                        .build())
                .baseUrl("https://jsonplaceholder.typicode.com")
                .build();
    }


    @GetMapping(value = "/viaProxy")
    public Mono<String> proxyTest(){

        WebClient webClient = webClientBuilderWithProxy();


        return webClient.get()
                .uri("/posts")  // Relative to the base URL
                .retrieve()
                .bodyToMono(String.class);

    }

    @GetMapping(value = "/notProxy")
    public Mono<String> withOutProxyTest(){

        WebClient webClient = WebClient.create("https://jsonplaceholder.typicode.com");

        return webClient.get()
                .uri("/posts")
                .retrieve()
                .bodyToMono(String.class);

    }


    @RequestMapping(value = "/test")
    public String test(){
        return "ok";
    }
}
