package com.bookslibrary.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@NotBlank(message = "The Title cannot be blank or empty")
    private String title;

	@NotBlank(message = "The Author cannot be blank or empty")
    private String author;

	@Pattern(regexp = "^[0-9]{13}$", message = "The ISBN should be a 13-digit number (e.g., 9788533302273). ")
	private String isbn;

	@Pattern(regexp = "^[0-9]{4}$", message = "Publication Year should be a 4-digit number (e.g., 1993).")
    private String publicationYear;

	@Column(length = 1000)
    private String description;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return this.author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getIsbn() {
		return this.isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public String getPublicationYear() {
		return this.publicationYear;
	}

	public void setPublicationYear(String publicationYear) {
		this.publicationYear = publicationYear;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
    
}
