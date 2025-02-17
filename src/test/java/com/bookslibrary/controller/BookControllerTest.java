package com.bookslibrary.controller;

import com.bookslibrary.entity.BookEntity;
import com.bookslibrary.repository.BookRepository;
import com.bookslibrary.service.BookService;
import com.bookslibrary.service.InsightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(BookController.class)
public class BookControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookRepository bookRepository;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private InsightService insightService;

    private BookEntity bookEntity;

    @BeforeEach
    void setUp(){
        bookEntity = new BookEntity();
        bookEntity.setId(1l);
        bookEntity.setTitle("The Lord of the Rings");
        bookEntity.setAuthor("J.R.R. Tolkien");
        bookEntity.setPublicationYear("2000");
        bookEntity.setIsbn("9999999999999");
    }

    @Test
    void testCreate() throws Exception{

        Mockito.when(bookRepository.save(any(BookEntity.class))).thenReturn(bookEntity);

        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"The Lord of the Rings\", \"author\": \"J.R.R. Tolkien\", \"publicationYear\": \"2000\", \"isbn\": \"9999999999999\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("The Lord of the Rings"));       

    }

    @Test
    void testGetAll() throws Exception{
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
        Mockito.when(bookRepository.findAll(pageable)).thenReturn(new PageImpl<>(Collections.singletonList(bookEntity)));

        mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "title,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("The Lord of the Rings"));
    }

    @Test
    void testGetBookById() throws Exception {
        Mockito.when(bookRepository.findById(anyLong())).thenReturn(Optional.of(bookEntity));

        mockMvc.perform(get("/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Lord of the Rings"));
    }

    @Test
    void testUpdate() throws Exception {
        Mockito.when(bookRepository.findById(anyLong())).thenReturn(Optional.of(bookEntity));
        Mockito.when(bookRepository.save(any(BookEntity.class))).thenReturn(bookEntity);

        mockMvc.perform(put("/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Updated Book\", \"author\": \"Updated Author\", \"publicationYear\": \"2000\", \"isbn\": \"9999999999999\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Book"));
    }

    @Test
    void testDelete() throws Exception {
        Mockito.when(bookRepository.existsById(anyLong())).thenReturn(true);

        mockMvc.perform(delete("/books/1"))
                .andExpect(status().isOk());
    }

    @Test
    void testFilterBooksBy() throws Exception {
        Mockito.when(bookService.filterBooksBy(any(), any())).thenReturn(Collections.singletonList(bookEntity));

        mockMvc.perform(get("/books/filterBy")
                .param("title", "The Lord of the Rings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("The Lord of the Rings"));
    }

    @Test
    void testGetBookInsights() throws Exception {
        Mockito.when(insightService.getBookInsights(anyLong())).thenReturn(bookEntity);

        mockMvc.perform(get("/books/1/ai-insights"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Lord of the Rings"));
    }
}
