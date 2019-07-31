package com.dugonski.urlshortener.impl.controller;

import com.dugonski.urlshortener.api.model.ShortenedURL;
import com.dugonski.urlshortener.api.service.URLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import java.net.URL;
import java.time.Clock;
import java.util.Optional;

@Controller
public class URLController {

    @Autowired
    private URLService service;

    @Autowired
    private Clock clock;

    @Autowired
    private HttpServletRequest request;

    @RequestMapping(value="/index.html", method = RequestMethod.GET)
    public ModelAndView index() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index");
        modelAndView.addObject("timeToLive", this.service.getExpiresAt());
        return modelAndView;
    }

    @PostMapping(path = "/create-url", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    @ResponseBody
    public ResponseEntity createUrl(@RequestParam(name = "url") String urlParam) {
        Optional<URL> url = this.service.safeConvertURL(urlParam);
        if(!url.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        ShortenedURL shortenedURL = this.service.persist(url.get());
        return ResponseEntity.ok().body(shortenedURL.toCreatedUrl(getBaseURL()));
    }

    @GetMapping(path = "/{hash}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public Object redirectUrl(@PathVariable(name = "hash") String hash) {
        Optional<ShortenedURL> url = this.service.retrieveURL(hash);
        RedirectView redirectView = new RedirectView();
        if(!url.isPresent()) {
            redirectView.setUrl("forward:/not-found");
            redirectView.setStatusCode(HttpStatus.NOT_FOUND);
            return redirectView;
        }
        else if(url.get().isExpired(this.clock)) {
            redirectView.setUrl("forward:/gone");
            redirectView.setStatusCode(HttpStatus.GONE);
            return redirectView;
        }
        redirectView.setUrl(url.get().getUrl());
        redirectView.setStatusCode(HttpStatus.FOUND);
        return redirectView;
    }

    private String getBaseURL() {
        return ServletUriComponentsBuilder.fromCurrentContextPath().build().toString();
    }

}
