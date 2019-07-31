package com.dugonski.urlshortener;

import com.dugonski.urlshortener.api.model.CreatedUrl;
import com.dugonski.urlshortener.api.service.URLService;
import com.dugonski.urlshortener.impl.component.Shortener;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Clock;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UrlShortenerTest {

    private static final String REDIRECT_URL = "https://api.ipify.org?format=json";

    @LocalServerPort
    private int port;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private Shortener shortener;

    @Autowired
    private Clock clock;

    @Autowired
    private URLService service;

    @Test
    public void shouldCreateURL() {
        String expectedHash = this.shortener.transform(REDIRECT_URL);
        ResponseEntity<CreatedUrl> createdUrl = doCreateUrl(REDIRECT_URL);
        assertNotNull(createdUrl.getBody());
        assertNotNull(createdUrl.getBody().getNewUrl().contains(expectedHash));
    }

    @Test
    public void shouldReturnBadRequestForCreatingInvalidURL() {
        ResponseEntity<CreatedUrl> createdUrl = doCreateUrl("http//malformed-URL");
        assertEquals(HttpStatus.BAD_REQUEST, createdUrl.getStatusCode());
    }

    @Test
    public void shouldRedirectAfterCreation() throws Exception {
        ResponseEntity<CreatedUrl> createdUrlResponseEntity = doCreateUrl(REDIRECT_URL);
        String expectedHash = extractHash(createdUrlResponseEntity.getBody());

        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(String.format("%s", expectedHash));
        this.mockMvc.perform(builder).andExpect(redirectedUrl(REDIRECT_URL)).andExpect(status().isFound());
    }

    @Test
    public void shouldReturnNotFoundForANonExistingHash() throws Exception {
        assertEquals(HttpStatus.NOT_FOUND, doRedirectUrl("a_non_existing_hash").getStatusCode());
    }

    @Test
    public void shouldReturnGoneForExpiredHash() throws Exception {
        int millisToExpire = -1 * 1000;
        this.service.setExpiresAt(millisToExpire);
        ResponseEntity<CreatedUrl> createdUrlResponseEntity = doCreateUrl(REDIRECT_URL);
        String expectedHash = extractHash(createdUrlResponseEntity.getBody());
        ResponseEntity<Void> response = doRedirectUrl(expectedHash);
        assertEquals(HttpStatus.GONE, response.getStatusCode());
    }

    private String getURL(String path) {
        return String.format("http://localhost:%s%s", this.port, path);
    }

    private String extractHash(CreatedUrl createdUrl) {
        return UriComponentsBuilder.fromUriString(createdUrl.getNewUrl()).build().getPath();
    }

    private ResponseEntity<CreatedUrl> doCreateUrl(String url) {
        String endpointURL = getURL(String.format("/create-url?url=%s", url));
        return this.restTemplate.postForEntity(endpointURL, null, CreatedUrl.class);
    }

    private ResponseEntity<Void> doRedirectUrl(String hash) {
        String endpointURL = getURL(String.format("/%s", hash));
        return this.restTemplate.getForEntity(endpointURL, Void.class);
    }

}
