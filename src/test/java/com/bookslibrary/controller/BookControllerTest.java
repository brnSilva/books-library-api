package com.bookslibrary.controller;

import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

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

    @InjectMocks
    private BookController bookController;

    private BookEntity bookEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        bookEntity = new BookEntity();
        bookEntity.setId(1L);
        bookEntity.setTitle("The Lord of the Rings");
        bookEntity.setAuthor("J.R.R. Tolkien");
        bookEntity.setPublicationYear("2000");
        bookEntity.setIsbn("9999999999999");

        mockMvc = MockMvcBuilders.standaloneSetup(bookController)
                .build();
    }

    @Test
    void testCreate_Success() throws Exception {
        Mockito.when(bookRepository.save(Mockito.any(BookEntity.class))).thenReturn(bookEntity);

        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"The Lord of the Rings\", \"author\": \"J.R.R. Tolkien\", \"publicationYear\": \"2000\", \"isbn\": \"9999999999999\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("The Lord of the Rings"));
    }

    @Test
    void testCreateInvalidTitle_MethodArgumentNotValidException() throws Exception {
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"\", \"author\": \"J.R.R. Tolkien\", \"publicationYear\": \"2000\", \"isbn\": \"9999999999999\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateInvalidIsbn_MethodArgumentNotValidException() throws Exception {
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"The Lord of the Rings\", \"author\": \"J.R.R. Tolkien\", \"publicationYear\": \"2000\", \"isbn\": \"12345\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testListPageableEmpty_Success() throws Exception{
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "title"));
        Mockito.when(bookService.findPageable("", pageable)).thenReturn(new PageImpl<>(Collections.singletonList(bookEntity)));

        mockMvc.perform(get("/books")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "title,asc"))
                .andExpect(status().isOk());
    }

    @Test
    void testGetById_Success() throws Exception {
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.of(bookEntity));

        mockMvc.perform(get("/books/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Lord of the Rings"));
    }

    @Test
    void testGetById_NotFound() throws Exception {
        Mockito.when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/books/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdate_Success() throws Exception {
        BookEntity updatedBook = new BookEntity();
        updatedBook.setId(1L);
        updatedBook.setTitle("Updated Book");
        updatedBook.setAuthor("Updated Author");
        updatedBook.setPublicationYear("2000");
        updatedBook.setIsbn("9999999999999");

        Mockito.when(bookService.updateBook(anyLong(), any(BookEntity.class))).thenReturn(updatedBook);

        mockMvc.perform(put("/books/{id}", 1L)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"Updated Book\", \"author\": \"Updated Author\", \"publicationYear\": \"2000\", \"isbn\": \"9999999999999\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Book"))
                .andExpect(jsonPath("$.author").value("Updated Author"))
                .andExpect(jsonPath("$.publicationYear").value("2000"))
                .andExpect(jsonPath("$.isbn").value("9999999999999"));
    }

    @Test
    void testDelete_Success() throws Exception {
        Mockito.when(bookRepository.existsById(1L)).thenReturn(true);

        mockMvc.perform(delete("/books/{id}", 1L))
                .andExpect(status().isOk());
    }

    @Test
    void testDelete_NotFound() throws Exception {
        Mockito.when(bookRepository.existsById(1L)).thenReturn(false);

        mockMvc.perform(delete("/books/{id}", 1L))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetBookInsights_Success() throws Exception {
        Mockito.when(insightService.getBookInsights(1L)).thenReturn(bookEntity);

        mockMvc.perform(get("/books/{id}/ai-insights", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("The Lord of the Rings"));
    }
}
