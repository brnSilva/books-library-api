package com.bookslibrary.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.bookslibrary.entity.BookEntity;
import com.bookslibrary.exception.ApiIntegrationAIException;
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


    public BookEntity getBookInsights(Long id) {

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
                
                bookEntity.setDescription(recoverInsight(response.getBody()));
                return bookEntity;
            }
            
            throw new ApiIntegrationAIException("Failed AI insights. Please, try again.");
        } catch (HttpClientErrorException e) {
            
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new ApiIntegrationAIException("Unauthorized access. Please check your API key.");
            } else if (e.getStatusCode() == HttpStatus.FORBIDDEN) {
                throw new ApiIntegrationAIException("Forbidden access. You might not have permission to access this resource.");
            } else if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ApiIntegrationAIException("Resource not found. Please check the URL.");
            } else {
                throw new ApiIntegrationAIException("Client error: " + e.getMessage());
            }
        } catch (HttpServerErrorException e) {

            throw new ApiIntegrationAIException("Server error: " + e.getMessage());
        } catch (Exception e) {

            throw new ApiIntegrationAIException("Error while calling AI Service.");
        }
        
    }

    private String buildPrompt(BookEntity bookEntity) {
        return String.format(prompt, bookEntity.getTitle(), 
                                    bookEntity.getAuthor());
    }

    private String recoverInsight(Map responseBody) throws Exception {
        
        if(responseBody != null && responseBody.containsKey("choices")){
            
            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");


            Optional<String> text = choices.stream()
                                            .filter(choice -> choice.containsKey("message"))
                                            .map(choice -> (Map<String, Object>) choice.get("message"))
                                            .map(message -> (String) message.get("content"))
                                            .findFirst();
            return text.get();
        }
        throw new ApiIntegrationAIException("No text found in the OpenAI response.");
    }
    
}
