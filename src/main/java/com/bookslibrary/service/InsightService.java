package com.bookslibrary.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.bookslibrary.entity.BookEntity;
import com.bookslibrary.exception.ResouceNotFoundException;
import com.bookslibrary.repository.BookRepository;

@Service
public class InsightService {
    
    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${external.ai.api.url}")
    private String externalAIUrl;

    @Value("${external.ai.api.key}")
    private String externalApiKey;

    @Value("${external.ai.api.prompt}")
    private String prompt;


    public Map<String, Object> getBookInsights(Long id) {

        BookEntity bookEntity = bookRepository.findById(id)
                                    .orElseThrow(() -> new ResouceNotFoundException("Book not found"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer "+ externalApiKey);

        Map<String, Object> request = new HashMap<>();
        request.put("model", "gpt-4o-mini");

        List<Map<String,String>> messages = new ArrayList<>();
        Map<String, String> messageContent = new HashMap<>();
        messageContent.put("role", "developer");
        messageContent.put("content", buildPrompt(bookEntity));
        messages.add(messageContent);
        request.put("messages", messages);
        
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try{
            ResponseEntity<Map> response = restTemplate.postForEntity(externalAIUrl, entity, Map.class);

            if(response.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> insights = response.getBody();
                insights.put("book", bookEntity);
                return insights;
            }
            
            throw new RuntimeException("Failed AI insights. Please, try again.");
        } catch (Exception e) {
            throw new RuntimeException("Error while calling AI Service.", e);
        }
        
    }

    private String buildPrompt(BookEntity bookEntity) {
        return String.format(prompt, bookEntity.getTitle(), 
                                    bookEntity.getAuthor(),
                                    bookEntity.getDescription());
    }
    
}
