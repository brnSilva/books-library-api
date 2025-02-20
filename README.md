# Books Library API
https://github.com/brnSilva/books-library-api

## Overview

The Books Library API is a Spring Boot application designed to manage a library of books. It provides endpoints for creating, updating, retrieving, and deleting book records, as well as generating AI insights for books using the OpenAI API.

## Build and Run Instructions

### Prerequisites

- Java 17 or later
- Maven 3.6.0 or later

### Build the application using Maven

Clone the repository:

```bash
git clone https://github.com/brnSilva/books-library-api
cd books-library-api
```

#### Environment Variables
The application requires several environment variables to be set for integration with the OpenAI API. These variables have default values but you need to set the **external.ai.api.key** as it is a sensitive piece of information.


 | Variable Name        |       Description                             | Default Value                                                                                         |
 |----------------------|-----------------------------------------------|-------------------------------------------------------------------------------------------------------|
 | OPENAI_API_URL       |   URL for the OpenAI API                      |   https://api.openai.com/v1/chat/completions                                                          |
 | OPENAI_API_KEY       |   API key for accessing the OpenAI API        |   **No default value, must be provided**                                                                  |
 | OPENAI_API_MODEL     |   Model to be used for AI insights            |   gpt-4o-mini                                                                                         |
 | OPENAI_API_ROLE      |   Role to be used for AI insights             |   developer                                                                                           |
 | OPENAI_API_PROMPT    |   Prompt template for generating AI insights  |   Please generate a detailed and engaging summary, including key plot points, character development, and significant themes. The summary should be up to 100 words for this book: Title: %s. Author: %s. |
 |                      |                                               |    |

You can set these variables in your application.properties or in an .env file in the root of the project.

```properties
# OpenAI
external.ai.api.url=${OPENAI_API_URL:https://api.openai.com/v1/chat/completions}
external.ai.api.key=${OPENAI_API_KEY}
external.ai.api.model=${OPENAI_API_MODEL:gpt-4o-mini}
external.ai.api.role=${OPENAI_API_ROLE:developer}
external.ai.api.prompt=${OPENAI_API_PROMPT:Please generate a detailed and engaging summary, including key plot points, character development, and significant themes. The summary should be up to 100 words for this book: Title: %s. Author: %s.}
```

#### Build the application using Maven:
```bash
mvn clean install
```
#### Run the application using Maven:
```bash
mvn spring-boot:run
```
#### Alternatively, you can run the application using the generated JAR file:
```bash
java -jar target/books-library-api-0.0.1-SNAPSHOT.jar
```

### API Documentation
The API documentation is available via Swagger UI. Once the application is running, you can access it at the following URL:

```bash
http://localhost:8080/swagger-ui.html
```

## Decisions developing:

**Spring Boot**: It is simple and easy to program and integrate with various components.
**OpenAI/Swagger**: It is extremely simply to use to documentation and provide a clear and interactive interface for API consumers.
**OpenAI**: I looked for free AI APIs to use in this project, but OpenAI made development much easier with documentation that was easy to read and understand.
**Exception Handling**: Custom exceptions are used to provide meaningful error messages and HTTP status codes and, in addition, centralize exceptions in a single place for future maintenance.
**Pagination**: Implemented for efficient handling and retrieval of large datasets. Frontend thanks. It is lighter to fetch smaller blocks of records.
**Filtering**: I opted for a general listing filter with query parameters to allow searching by author OR title, facilitating flexible and efficient search capabilities on the Front end.
