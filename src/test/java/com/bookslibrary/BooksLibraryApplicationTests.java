package com.bookslibrary;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class BooksLibraryApplicationTests {

	@Autowired
	private BooksLibraryApplication booksLibraryApplication;
	
	@Test
	void contextLoads() {
		assertThat(booksLibraryApplication).isNotNull();
	}

}
