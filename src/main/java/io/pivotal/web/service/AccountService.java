package io.pivotal.web.service;

import com.newrelic.api.agent.Trace;
import io.pivotal.web.domain.Account;
import io.pivotal.web.domain.Portfolio;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction.oauth2AuthorizedClient;

@Service
@RefreshScope
public class AccountService {
    private static final Logger logger = LoggerFactory
            .getLogger(AccountService.class);

    @Autowired
    private WebClient webClient;

    @Value("${pivotal.accountsService.name}")
    private String accountsService;


    @Trace(async = true)
    public void createAccount(Account account, OAuth2AuthorizedClient oAuth2AuthorizedClient ) {
        logger.debug("Creating account ");
        String status = webClient
                .post()
                .uri("//" + accountsService + "/accounts/")
                .contentType(MediaType.APPLICATION_JSON)
                .syncBody(account)
                .attributes(oauth2AuthorizedClient(oAuth2AuthorizedClient))
                .retrieve()
                .bodyToMono(String.class)
                .block();
       // String status = oAuth2RestTemplate.postForObject("//" + accountsService + "/accounts/", account, String.class);
        logger.info("Status from registering account is " + status);
    }


    @Trace(async = true)
    public Flux<Account> getAccounts(OAuth2AuthorizedClient oAuth2AuthorizedClient) {
        logger.debug("Looking for accounts");
//        ParameterizedTypeReference<List<Account>> typeRef = new ParameterizedTypeReference<List<Account>>() {};

//        Publisher<List<Account>> accountsPublisher = webClient
//                .get()
//                .uri("//" + accountsService + "/accounts")
//                .attributes(oauth2AuthorizedClient(oAuth2AuthorizedClient))
//                .retrieve()
//                .bodyToMono(typeRef);

//        List<Account> accounts = HystrixCommands
//                .from( accountsPublisher )
//                .eager()
//                .commandName("accounts")
//                .fallback(Flux.just(getAccountsFallback()))
//                .toMono()
//                .block();
        Consumer<Map<String, Object>> attrs = oauth2AuthorizedClient(oAuth2AuthorizedClient);
		Flux<Account> accounts = webClient.get().uri("//" + accountsService + "/accounts").attributes(attrs).retrieve().bodyToFlux(Account.class);
        return accounts;
    }

    public List<Account> getAccountsFallback() {
        logger.warn("Invoking fallback for getAccount");
        return new ArrayList<>();
    }


    @Trace(async = true)
    public Flux<Account> getAccountsByType(String type, OAuth2AuthorizedClient oAuth2AuthorizedClient) {
        logger.debug("Looking for account with type: " + type);
//        ParameterizedTypeReference<List<Account>> typeRef = new ParameterizedTypeReference<List<Account>>() {};
//        Publisher<List<Account>> accountsPublisher = webClient
//                .get()
//                .uri("//" + accountsService + "/accounts?type=" + type)
//                .attributes(oauth2AuthorizedClient(oAuth2AuthorizedClient))
//                .retrieve()
//                .bodyToMono(typeRef);
//
//        List<Account> accounts = HystrixCommands
//                .from( accountsPublisher )
//                .eager()
//                .commandName("accounts")
//                .fallback(Flux.just(getAccountsFallback()))
//                .toMono()
//                .block();
        
       // Account[] accounts = oAuth2RestTemplate.getForObject("//" + accountsService + "/accounts?type={type}", Account[].class, type);
        Consumer<Map<String, Object>> attrs = oauth2AuthorizedClient(oAuth2AuthorizedClient);
		Flux<Account> accounts = webClient.get().uri("//" + accountsService + "/accounts?type=" + type).attributes(attrs).retrieve().bodyToFlux(Account.class);
        return accounts;
    }

}
