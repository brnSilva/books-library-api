package com.bookslibrary.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DirectFieldBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;

@WebMvcTest(controllers = GlobalExceptionHandler.class)
public class GlobalExceptionHandlerTest{

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    public void setUp() {
        globalExceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    public void testHandleResourceNotFoundException() {
        ResouceNotFoundException ex = new ResouceNotFoundException("Resource not found");
        ResponseEntity<?> response = globalExceptionHandler.handleResourceNotFoundException(ex, null);
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Resource not found", ((Map<String, String>) response.getBody()).get("error"));
    }

    
    @Test
    public void testHandleValidationExceptions() {
        BindingResult bindingResult = new DirectFieldBindingResult(new Object(), "target");
        bindingResult.addError(new FieldError("objectName", "fieldName", "Validation failed"));

        MethodArgumentNotValidException ex = new MethodArgumentNotValidException(null, bindingResult);
        ResponseEntity<?> response = globalExceptionHandler.handleValidationExceptions(ex);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Validation failed", ((Map<String, String>) response.getBody()).get("fieldName"));
    }

    @Test
    public void testHandleIllegalArgumentException() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument");
        ResponseEntity<?> response = globalExceptionHandler.handleIllegalArgumentException(ex);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid argument", ((Map<String, String>) response.getBody()).get("error"));
    }

    @Test
    public void testHandleNumberFormatException() {
        NumberFormatException ex = new NumberFormatException("Number format error");
        ResponseEntity<?> response = globalExceptionHandler.handleNumberFormatException(ex);
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Number format error", ((Map<String, String>) response.getBody()).get("error"));
    }

    @Test
    public void testHandleMethodArgumentTypeMismatchException() {
        String parameterName = "param";
        String value = "invalidValue";
        MethodArgumentTypeMismatchException ex = new MethodArgumentTypeMismatchException(
                value, Integer.class, parameterName, null, 
                new IllegalArgumentException("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer'")
        );

        ResponseEntity<?> response = globalExceptionHandler.handleMethodArgumentTypeMismatchException(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(((Map<String, String>) response.getBody()).get("error").contains("Failed to convert value of type 'java.lang.String' to required type 'java.lang.Integer'"));
    }



    @Test
    public void testHandleAIIntegrationException() {
        ApiIntegrationAIException ex = new ApiIntegrationAIException("AI integration error");
        ResponseEntity<?> response = globalExceptionHandler.handleAIIntegrationException(ex);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("AI integration error", ((Map<String, String>) response.getBody()).get("error"));
    }

    @Test
    public void testHandleGlobalException() {
        Exception ex = new Exception("Global error");
        ResponseEntity<?> response = globalExceptionHandler.handleGlobalException(ex, null);
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error: Global error", ((Map<String, String>) response.getBody()).get("error"));
    }
}
