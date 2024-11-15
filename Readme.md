
# Store Management API - ING Career

## Overview
The Store Management API provides a structured solution for managing products within a store, including functionalities such as adding, retrieving, updating, and deleting products. Authentication and authorization features are implemented to secure access.

## Requirements
- **Java** 11+
- **Docker** (for containerization)
- **PostgreSQL** (locally or within Docker)
- **Artifactory Vault** (for secure credential storage; environment variables with default values can also be used)

## Setup and Configuration

### 1. Clone Repository and Initialize Database
Ensure PostgreSQL is running and update database connection details in `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/storemanager
    username: admin
    password: secret
  jpa:
    hibernate:
      ddl-auto: update
```

### 2. Run PostgreSQL with Docker Compose
To start the PostgreSQL database with Docker Compose, use:
```bash
docker-compose up
```

### 3. Start the Application
```bash
./mvnw spring-boot:run
```

Alternatively, build and run:
```bash
./mvnw clean package
java -jar target/StoreManagerApplication.jar --server.port=8081
```

## API Endpoints

### Product Management

1. **Add Product**
    - `POST /api/products`
    - **Body**:
      ```json
      {
        "name": "Carnati",
        "price": 120.99,
        "description": "Acesta este un demo functional pentru store manager ING"
      }
      ```
    - **Headers**:
        - `Content-Type: application/json`
    - **Authentication**: Basic Auth (username: `admin`, password: `password`)

2. **Update Product Price**
    - `PUT /api/products/{id}/price`
    - **Body**:
      ```json
      {
        "newPrice": 21.01
      }
      ```
    - **Response**:
      ```json
      {
        "id": 12,
        "name": "Carnati",
        "price": 21.01,
        "description": "Acesta este un demo functional pentru store manager ING",
        "createdDate": "2024-11-11T16:48:48.609413",
        "updatedDate": "2024-11-11T16:49:25.989318"
      }
      ```

3. **Partially Update Product**
    - `PATCH /api/products/{id}`
    - **Body Options**:
      ```json
      { "description": "Updated product description - am nevoie de carnati" }
      ```

4. **Retrieve Product**
    - `GET /api/products/{id}`
    - **Response**:
      ```json
      {
        "id": 12,
        "name": "Carnati",
        "price": 21.01,
        "description": "Updated product description - am nevoie de carnati",
        "createdDate": "2024-11-11T16:48:48.609413",
        "updatedDate": "2024-11-11T16:49:47.647494"
      }
      ```

5. **View All Products**
    - `GET /api/products/all`
    - **Sample Response**:
      ```json
      [
        {
          "id": 6,
          "name": "Biscuiti",
          "price": 19.99,
          "description": "Demo description",
          "createdDate": null,
          "updatedDate": null
        },
        ...
      ]
      ```

6. **Search Products**
    - `GET /api/products/search?name=Covrigi`

7. **Pagination**
    - `GET /api/products/all?page=0&size=10`
  
8. **Delete Product**
    - `DELETE /api/products/8`

## AI Integration
- Added Generation content for product description with OpenAIService


## Future AI Integration
- **Retrieval-Augmented Generation (RAG)** over an LLM may be added in the future using OpenAI embeddings, subject to project timing.
