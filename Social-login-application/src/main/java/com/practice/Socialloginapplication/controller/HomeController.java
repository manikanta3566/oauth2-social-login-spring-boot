package com.practice.Socialloginapplication.controller;

import com.nimbusds.jose.shaded.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Controller
public class HomeController {

    @Autowired
    private OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/home")
    public String homePage(Model model, @AuthenticationPrincipal OAuth2User oAuth2User){
        if(Objects.nonNull(oAuth2User)){
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
            OAuth2AuthorizedClient oAuth2AuthorizedClient = oAuth2AuthorizedClientService.loadAuthorizedClient(oAuth2AuthenticationToken.getAuthorizedClientRegistrationId(),
                    oAuth2AuthenticationToken.getName());
            String tokenValue = oAuth2AuthorizedClient.getAccessToken().getTokenValue();
            //call google apis with access token
            String url="https://www.googleapis.com/oauth2/v1/userinfo";
            HttpHeaders httpHeaders=new HttpHeaders();
            httpHeaders.add("Authorization","Bearer "+tokenValue);
            HttpEntity entity=new HttpEntity<>(httpHeaders);
            ResponseEntity<JSONObject> exchange = restTemplate.exchange(url, HttpMethod.GET, entity,JSONObject.class);
            JSONObject json = exchange.getBody();
            String picture = (String) json.get("picture");
            String email = (String) json.get("email");
            model.addAttribute("email",email);
            model.addAttribute("picture",picture);
            model.addAttribute("name",oAuth2User.getAttribute("name"));
        }
        return "home";
    }
}
