# Puppies API

**Puppies API** is a Spring Boot RESTful service for sharing dog pictures, similar to Instagram.

## 🚀 Setup

1. **Clone the repository**

   ```bash
   git clone https://github.com/your-username/puppies-api.git
   cd puppies-api
   ```

2. **Run the application** (Java 11+)

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
