# Puppies API

**Puppies API** is a Spring Boot RESTful service for sharing dog pictures, similar to Instagram.

## 🚀 Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/Ziegeraze/puppies.git
   cd puppies
   ```

2. **Run the application** (Java 17)

   ```bash
   mvn spring-boot:run
   ```

3. **Access H2 console**

   * URL: `http://localhost:8080/h2-console`
   * JDBC URL: `jdbc:h2:mem:puppies`
   * User: `sa`
   * Password: (leave blank)

## ✅ Running Tests

```bash
mvn test
```

## 📚 API Endpoints

| Path                    | Method | Request Body   | Response Body        | Description                          |
| ----------------------- | ------ | -------------- | -------------------- | ------------------------------------ |
| `/api/users`            | POST   | `UserRequest`  | `UserResponse`       | Create a new user                    |
| `/api/auth/login`       | POST   | `AuthRequest`  | `AuthResponse`       | Authenticate and obtain JWT          |
| `/api/posts`            | POST   | `PostRequest`  | `PostResponse`       | Create a new post                    |
| `/api/posts/feed`       | GET    | —              | `List<PostResponse>` | Get feed (posts sorted by date desc) |
| `/api/posts/{id}`       | GET    | —              | `PostResponse`       | Get details of a single post         |
| `/api/posts/{id}/likes` | POST   | `userId` query | —                    | Like a post                          |
| `/api/posts/{id}/likes` | DELETE | `userId` query | —                    | Unlike a post                        |
| `/api/users/{id}`       | GET    | —              | `UserResponse`       | Get user profile                     |
| `/api/users/{id}/posts` | GET    | —              | `List<PostResponse>` | List posts created by a user         |
| `/api/users/{id}/likes` | GET    | —              | `List<PostResponse>` | List posts liked by a user           |

## 🎉 Highlights

* **Lombok** to reduce boilerplate (getters/setters, builders).
* **JWT** for stateless authentication.
* **Layered architecture** (controller → service → repository).
* **Validation** with `@Valid` and `@ControllerAdvice`.
* **Comprehensive tests** with MockMvc covering success and error cases.

## ⚠️ Limitations

* **H2 in-memory database** (not production-ready).
* No **pagination** on list endpoints.
* Image uploads are simulated via URLs.
* No role-based access control.

## 🚧 Next Steps (optional)

* Add a **Dockerfile** + **docker-compose** to containerize the API and H2.
* Integrate **Swagger/OpenAPI** (springdoc) for auto-generated docs.
* Set up **CI/CD** (GitHub Actions) to build and test on each push.

---

*This README provides a quick guide for developing and testing the Puppies API.*

## 🧪 Demo Flow with Postman

This guide walks you through testing the Puppies API using Postman, including setting up variables and automating data extraction.

**I. Preparation & Postman Setup:**

1.  **Ensure Prerequisites:**
    *   The Spring Boot application is running (`mvn spring-boot:run`).
    *   Postman is installed and open.
    *   (Optional) Open the H2 console (`http://localhost:8080/h2-console`) to observe data changes.
    *   (Optional) Open the Swagger UI (`http://localhost:8080/swagger-ui.html`) for API exploration.

2.  **Create Postman Collection:**
    *   In Postman, create a new collection named "Puppies API Demo".

3.  **Set Up Initial Collection Variables:**
    *   Go to your "Puppies API Demo" collection, and in the "Variables" tab, define the following. These will be updated by test scripts as you proceed.
        *   `BASE_URL`: `http://localhost:8080/api`
        *   `USER_1_ID`: (leave blank)
        *   `USER_1_TOKEN`: (leave blank)
        *   `USER_2_ID`: (leave blank)
        *   `USER_2_TOKEN`: (leave blank)
        *   `POST_1_ID`: (leave blank)
        *   `POST_2_ID`: (leave blank)
        *   `POST_3_ID`: (leave blank)

**II. Demo Steps with Postman:**

**Step 1: Create User 1 (Alice)**

*   **Action:** Create the first user, Alice.
*   **Postman Request:**
    *   **Method:** `POST`
    *   **URL:** `{{BASE_URL}}/users`
    *   **Body (raw, JSON):**
        ```json
        {
            "name": "alice",
            "password": "password123",
            "email": "alice@example.com"
        }
        ```
*   **Postman Tests (JavaScript):**
    ```javascript
    // In the "Tests" tab for this request
    if (pm.response.code === 200) {
        const responseBody = pm.response.json();
        pm.collectionVariables.set("USER_1_ID", responseBody.id);
        console.log("USER_1_ID set to: " + responseBody.id);
    } else {
        console.error("Failed to create User 1. Status: " + pm.response.code + ", Body: " + pm.response.text());
    }
    ```
*   **Expected Response:** `200 OK` with user details. `USER_1_ID` variable will be populated.

**Step 2: Create User 2 (Bob)**

*   **Action:** Create a second user, Bob.
*   **Postman Request:**
    *   **Method:** `POST`
    *   **URL:** `{{BASE_URL}}/users`
    *   **Body (raw, JSON):**
        ```json
        {
            "name": "bob",
            "password": "password456",
            "email": "bob@example.com"
        }
        ```
*   **Postman Tests (JavaScript):**
    ```javascript
    // In the "Tests" tab for this request
    if (pm.response.code === 200) {
        const responseBody = pm.response.json();
        pm.collectionVariables.set("USER_2_ID", responseBody.id);
        console.log("USER_2_ID set to: " + responseBody.id);
    } else {
        console.error("Failed to create User 2. Status: " + pm.response.code + ", Body: " + pm.response.text());
    }
    ```
*   **Expected Response:** `200 OK` with user details. `USER_2_ID` variable will be populated.

**Step 3: Authenticate User 1 (Alice)**

*   **Action:** Log in as Alice to get her JWT.
*   **Postman Request:**
    *   **Method:** `POST`
    *   **URL:** `{{BASE_URL}}/auth/login`
    *   **Body (raw, JSON):**
        ```json
        {
            "username": "alice",
            "password": "password123"
        }
        ```
*   **Postman Tests (JavaScript):**
    ```javascript
    // In the "Tests" tab for this request
    if (pm.response.code === 200) {
        const responseBody = pm.response.json();
        pm.collectionVariables.set("USER_1_TOKEN", responseBody.token);
        console.log("USER_1_TOKEN set.");
    } else {
        console.error("Failed to authenticate User 1. Status: " + pm.response.code + ", Body: " + pm.response.text());
    }
    ```
*   **Expected Response:** `200 OK` with a JWT. `USER_1_TOKEN` variable will be populated.

**Step 4: Authenticate User 2 (Bob)**

*   **Action:** Log in as Bob to get his JWT.
*   **Postman Request:**
    *   **Method:** `POST`
    *   **URL:** `{{BASE_URL}}/auth/login`
    *   **Body (raw, JSON):**
        ```json
        {
            "username": "bob",
            "password": "password456"
        }
        ```
*   **Postman Tests (JavaScript):**
    ```javascript
    // In the "Tests" tab for this request
    if (pm.response.code === 200) {
        const responseBody = pm.response.json();
        pm.collectionVariables.set("USER_2_TOKEN", responseBody.token);
        console.log("USER_2_TOKEN set.");
    } else {
        console.error("Failed to authenticate User 2. Status: " + pm.response.code + ", Body: " + pm.response.text());
    }
    ```
*   **Expected Response:** `200 OK` with a JWT. `USER_2_TOKEN` variable will be populated.

**Step 5: User 1 (Alice) Creates Post 1**

*   **Action:** Alice creates her first post.
*   **Postman Request:**
    *   **Method:** `POST`
    *   **URL:** `{{BASE_URL}}/posts`
    *   **Headers:** `Authorization`: `Bearer {{USER_1_TOKEN}}`
    *   **Body (raw, JSON):**
        ```json
        {
            "imageUrl": "http://example.com/puppy1.jpg",
            "caption": "Alice's first cute puppy!"
        }
        ```
*   **Postman Tests (JavaScript):**
    ```javascript
    // In the "Tests" tab for this request
    if (pm.response.code === 200) {
        const responseBody = pm.response.json();
        pm.collectionVariables.set("POST_1_ID", responseBody.id);
        console.log("POST_1_ID set to: " + responseBody.id);
    } else {
        console.error("Failed to create Post 1. Status: " + pm.response.code + ", Body: " + pm.response.text());
    }
    ```
*   **Expected Response:** `200 OK` with post details. `POST_1_ID` variable will be populated.

**Step 6: User 1 (Alice) Creates Post 2**

*   **Action:** Alice creates another post.
*   **Postman Request:**
    *   **Method:** `POST`
    *   **URL:** `{{BASE_URL}}/posts`
    *   **Headers:** `Authorization`: `Bearer {{USER_1_TOKEN}}`
    *   **Body (raw, JSON):**
        ```json
        {
            "imageUrl": "http://example.com/puppy2.jpg",
            "caption": "Alice's second adorable puppy!"
        }
        ```
*   **Postman Tests (JavaScript):**
    ```javascript
    // In the "Tests" tab for this request
    if (pm.response.code === 200) {
        const responseBody = pm.response.json();
        pm.collectionVariables.set("POST_2_ID", responseBody.id);
        console.log("POST_2_ID set to: " + responseBody.id);
    } else {
        console.error("Failed to create Post 2. Status: " + pm.response.code + ", Body: " + pm.response.text());
    }
    ```
*   **Expected Response:** `200 OK` with post details. `POST_2_ID` variable will be populated.

**Step 7: User 2 (Bob) Creates Post 3**

*   **Action:** Bob creates a post.
*   **Postman Request:**
    *   **Method:** `POST`
    *   **URL:** `{{BASE_URL}}/posts`
    *   **Headers:** `Authorization`: `Bearer {{USER_2_TOKEN}}`
    *   **Body (raw, JSON):**
        ```json
        {
            "imageUrl": "http://example.com/puppy3.jpg",
            "caption": "Bob's playful puppy!"
        }
        ```
*   **Postman Tests (JavaScript):**
    ```javascript
    // In the "Tests" tab for this request
    if (pm.response.code === 200) {
        const responseBody = pm.response.json();
        pm.collectionVariables.set("POST_3_ID", responseBody.id);
        console.log("POST_3_ID set to: " + responseBody.id);
    } else {
        console.error("Failed to create Post 3. Status: " + pm.response.code + ", Body: " + pm.response.text());
    }
    ```
*   **Expected Response:** `200 OK` with post details. `POST_3_ID` variable will be populated.

**Step 8: Fetch User Feed (Authenticated as Alice)**

*   **Action:** Alice views her feed. It should show all posts, newest first.
*   **Postman Request:**
    *   **Method:** `GET`
    *   **URL:** `{{BASE_URL}}/posts/feed`
    *   **Headers:** `Authorization`: `Bearer {{USER_1_TOKEN}}`
*   **Expected Response:** `200 OK` with a list of posts (Post 3, then Post 2, then Post 1).
    *   *(Optional Postman Test: You can add assertions here to check the order and content of the feed.)*

**Step 9: Fetch Details of an Individual Post (Post 1)**

*   **Action:** View details of Alice's first post.
*   **Postman Request:**
    *   **Method:** `GET`
    *   **URL:** `{{BASE_URL}}/posts/{{POST_1_ID}}`
    *   **Headers:** `Authorization`: `Bearer {{USER_1_TOKEN}}` (or any valid token, or unauthenticated if GET /api/posts/{id} is public)
*   **Expected Response:** `200 OK` with details of Post 1.

**Step 10: User 1 (Alice) Likes Post 3 (Bob's Post)**

*   **Action:** Alice likes Bob's post.
*   **Postman Request:**
    *   **Method:** `POST`
    *   **URL:** `{{BASE_URL}}/posts/{{POST_3_ID}}/likes?userId={{USER_1_ID}}`
    *   **Headers:** `Authorization`: `Bearer {{USER_1_TOKEN}}`
*   **Expected Response:** `200 OK` (empty body).

**Step 11: User 2 (Bob) Likes Post 1 (Alice's Post)**

*   **Action:** Bob likes Alice's first post.
*   **Postman Request:**
    *   **Method:** `POST`
    *   **URL:** `{{BASE_URL}}/posts/{{POST_1_ID}}/likes?userId={{USER_2_ID}}`
    *   **Headers:** `Authorization`: `Bearer {{USER_2_TOKEN}}`
*   **Expected Response:** `200 OK`.

**Step 12: User 1 (Alice) Likes Post 1 (Her Own Post)**

*   **Action:** Alice likes her own first post.
*   **Postman Request:**
    *   **Method:** `POST`
    *   **URL:** `{{BASE_URL}}/posts/{{POST_1_ID}}/likes?userId={{USER_1_ID}}`
    *   **Headers:** `Authorization`: `Bearer {{USER_1_TOKEN}}`
*   **Expected Response:** `200 OK`.

**Step 13: User 1 (Alice) Tries to Like Post 1 Again (Idempotency Check)**

*   **Action:** Alice attempts to like her own first post again.
*   **Postman Request:**
    *   **Method:** `POST`
    *   **URL:** `{{BASE_URL}}/posts/{{POST_1_ID}}/likes?userId={{USER_1_ID}}`
    *   **Headers:** `Authorization`: `Bearer {{USER_1_TOKEN}}`
*   **Expected Response:** `200 OK`. (The like count should not increase, no error should occur).

**Step 14: Fetch User 1's (Alice's) Profile**

*   **Action:** View Alice's profile details.
*   **Postman Request:**
    *   **Method:** `GET`
    *   **URL:** `{{BASE_URL}}/users/{{USER_1_ID}}`
    *   **Headers:** `Authorization`: `Bearer {{USER_1_TOKEN}}` (or any valid token)
*   **Expected Response:** `200 OK` with Alice's user details.

**Step 15: Fetch Posts Made by User 1 (Alice)**

*   **Action:** View all posts created by Alice.
*   **Postman Request:**
    *   **Method:** `GET`
    *   **URL:** `{{BASE_URL}}/users/{{USER_1_ID}}/posts`
    *   **Headers:** `Authorization`: `Bearer {{USER_1_TOKEN}}` (or any valid token)
*   **Expected Response:** `200 OK` with a list of Alice's posts (Post 2, then Post 1).

**Step 16: Fetch Posts Liked by User 1 (Alice)**

*   **Action:** View all posts liked by Alice.
*   **Postman Request:**
    *   **Method:** `GET`
    *   **URL:** `{{BASE_URL}}/users/{{USER_1_ID}}/likes`
    *   **Headers:** `Authorization`: `Bearer {{USER_1_TOKEN}}` (or any valid token)
*   **Expected Response:** `200 OK` with a list of posts liked by Alice (should include Post 3 and Post 1).

**Step 17: User 1 (Alice) Unlikes Post 3 (Bob's Post)**

*   **Action:** Alice unlikes Bob's post.
*   **Postman Request:**
    *   **Method:** `DELETE`
    *   **URL:** `{{BASE_URL}}/posts/{{POST_3_ID}}/likes?userId={{USER_1_ID}}`
    *   **Headers:** `Authorization`: `Bearer {{USER_1_TOKEN}}`
*   **Expected Response:** `200 OK`.

**Step 18: Fetch Posts Liked by User 1 (Alice) - After Unlike**

*   **Action:** View Alice's liked posts again.
*   **Postman Request:**
    *   **Method:** `GET`
    *   **URL:** `{{BASE_URL}}/users/{{USER_1_ID}}/likes`
    *   **Headers:** `Authorization`: `Bearer {{USER_1_TOKEN}}`
*   **Expected Response:** `200 OK`. Post 3 should no longer be in this list; only Post 1 should remain.

**Step 19: Show Validation Error (Optional)**

*   **Action:** Attempt to create a user with invalid data (e.g., missing username).
*   **Postman Request:**
    *   **Method:** `POST`
    *   **URL:** `{{BASE_URL}}/users`
    *   **Body (raw, JSON):**
        ```json
        {
            "password": "password123",
            "email": "invalid@example.com"
        }
        ```
*   **Expected Response:** `400 Bad Request` with error details.

**Step 20: Show Not Found Error (Optional)**

*   **Action:** Attempt to fetch a non-existent post.
*   **Postman Request:**
    *   **Method:** `GET`
    *   **URL:** `{{BASE_URL}}/posts/9999` (assuming 9999 is not a valid post ID)
    *   **Headers:** `Authorization`: `Bearer {{USER_1_TOKEN}}`
*   **Expected Response:** `404 Not Found`.

**Step 21: Show Swagger UI (Optional)**

*   **Action:** Open a web browser and navigate to `http://localhost:8080/swagger-ui.html`.
*   **Expected:** The Swagger UI page should load, allowing interactive exploration of the API endpoints.

---
By following these integrated steps, you can efficiently test the Puppies API using Postman, leveraging its features for variable management and automated data extraction.
