package com.bookslibrary.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.bookslibrary.entity.BookEntity;
import com.bookslibrary.exception.ApiIntegrationAIException;
import com.bookslibrary.exception.ResouceNotFoundException;

import com.bookslibrary.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class InsightServiceTest {

    @Mock
    private BookRepository bookRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    @Autowired
    private InsightService insightService;

    private BookEntity bookEntity;

    @BeforeEach
    void setUp() {
        
        bookEntity = new BookEntity();
        bookEntity.setId(1L);
        bookEntity.setTitle("The Hobbit");
        bookEntity.setAuthor("J.R.R. Tolkien");
        bookEntity.setIsbn("1111111111111");
        bookEntity.setPublicationYear("2001");

    }
    
    @Test
    void testGetBookInsights_Success() throws Exception {

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(bookEntity));

        Map<String, Object> response = new HashMap<>();
        Map<String, Object> message = new HashMap<>();
        message.put("message", Map.of("content", "Description here to test."));
        response.put("choices", List.of(message));
        
        when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), any(Class.class)))
                .thenReturn(new ResponseEntity<>(response, HttpStatus.OK));

        BookEntity result = insightService.getBookInsights(1L);

        assertThat(result.getDescription()).isEqualTo("Description here to test.");
    }

    @Test
    void testGetBookInsights_BookNotFound() {

        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResouceNotFoundException.class, () -> insightService.getBookInsights(1L));
    }

    @Test
    void testGetBookInsights_Unauthorized() {
        
        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(bookEntity));
        doThrow(new HttpClientErrorException(HttpStatus.UNAUTHORIZED))
                .when(restTemplate).postForEntity(anyString(), any(HttpEntity.class), any(Class.class));

        assertThrows(ApiIntegrationAIException.class, () -> insightService.getBookInsights(1L));
    }

    @Test
    void testGetBookInsights_ServerError() {

        when(bookRepository.findById(anyLong())).thenReturn(Optional.of(bookEntity));
        doThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR))
                .when(restTemplate).postForEntity(anyString(), any(HttpEntity.class), any(Class.class));

        assertThrows(ApiIntegrationAIException.class, () -> insightService.getBookInsights(1L));
    }
}
