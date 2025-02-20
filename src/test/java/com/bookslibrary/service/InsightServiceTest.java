package com.bookslibrary.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;


import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.RestTemplate;

import com.bookslibrary.entity.BookEntity;
import com.bookslibrary.exception.ApiIntegrationAIException;
import com.bookslibrary.exception.ResouceNotFoundException;

import com.bookslibrary.repository.BookRepository;

import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.web.client.MockRestServiceServer;

@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest
@ActiveProfiles("test")
public class InsightServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    @Autowired
    private InsightService insightService;

    private BookEntity bookEntity;

    private MockRestServiceServer mockServer;


    @Value("${external.ai.api.url}")
    private String externalAIUrl;

    @Value("${external.ai.api.key}")
    private String externalApiKey;

    @Value("${external.ai.api.model}")
    private String externalApiModel;

    @Value("${external.ai.api.role}")
    private String externalApiRole;

    @Value("${external.ai.api.prompt}")
    private String prompt;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        bookEntity = new BookEntity();
        bookEntity.setId(anyLong());
        bookEntity.setTitle("The Hobbit");
        bookEntity.setAuthor("J.R.R. Tolkien");
        bookEntity.setIsbn("1111111111111");
        bookEntity.setPublicationYear("2001");

        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    void testGetBookInsights_Success() throws Exception {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(bookEntity));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + externalApiKey);

        Map<String, Object> response = new HashMap<>();
        Map<String, String> messageContent = new HashMap<>();
        messageContent.put("role", "assistant");
        messageContent.put("content", "This is a sample insight description.");
        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(messageContent);
        response.put("choices", Collections.singletonList(Collections.singletonMap("message", messageContent)));

        ResponseEntity<Map> mockResponse = new ResponseEntity<>(response, HttpStatus.OK);

        when(restTemplate.postForEntity(any(String.class), any(HttpEntity.class), any(Class.class)))
                .thenReturn(mockResponse);

        BookEntity result = insightService.getBookInsights(anyLong());

        assertNotNull(result);
        assertEquals("This is a sample insight description.", result.getDescription());
    }

    @Test
    void testGetBookInsights_NotFound() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResouceNotFoundException.class, () -> {
            insightService.getBookInsights(anyLong());
        });
    }

    @Test
    void testGetBookInsights_Unauthorized() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(bookEntity));

        mockServer.expect(requestTo(externalAIUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.UNAUTHORIZED));

        assertThrows(ApiIntegrationAIException.class, () -> {
            insightService.getBookInsights(anyLong());
        });
    }

    @Test
    void testGetBookInsights_Forbidden() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(bookEntity));

        mockServer.expect(requestTo(externalAIUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.FORBIDDEN));

        assertThrows(ApiIntegrationAIException.class, () -> {
            insightService.getBookInsights(anyLong());
        });
    }

    @Test
    void testGetBookInsights_InternalServerError() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(bookEntity));

        mockServer.expect(requestTo(externalAIUrl))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        assertThrows(ApiIntegrationAIException.class, () -> {
            insightService.getBookInsights(anyLong());
        });
    }
}