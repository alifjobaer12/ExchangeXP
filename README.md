# 🚀 ExchangeXP

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

> **ExchangeXP** is the backend API for a team project built using **Spring Boot** and **MongoDB**.  
> It manages business logic, authentication, and database operations for user and blog management.

---

## 🌟 About The Project

The **ExchangeXP Backend** serves as the core of the ExchangeXP ecosystem.  
It provides RESTful APIs to handle users, authentication, and content management — all powered by Spring Boot and MongoDB.

---

## 🛠️ Built With

- **[Java](https://www.java.com/):** Core programming language
- **[Spring Boot](https://spring.io/projects/spring-boot):** Backend application framework
- **[Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb):** Database interaction and persistence
- **[Spring Security](https://spring.io/projects/spring-security):** Authentication & authorization
- **[Maven](https://maven.apache.org/):** Dependency management and build automation

---

## ✨ Features

- 🔐 User registration, authentication, and management
- 📝 Blog post creation, retrieval, updating, and deletion (CRUD)
- 🧩 Admin endpoints for managing users and content
- ❤️ Health check endpoint for monitoring service status

---

## 🚀 Getting Started

Follow these steps to run the project locally.

### Prerequisites

Ensure the following are installed on your machine:

- JDK 17 or higher
- Apache Maven
- MongoDB (local instance or MongoDB Atlas cloud connection)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/alifjobaer12/ExchangeXP.git
   ```
2. **Navigate to the project directory**
   ```bash
   cd ExchangeXP
   ```
3. **Configure MongoDB connection**
    - Open `src/main/resources/application.properties` (or `application.yml`)
    - Set your MongoDB URI, for example:
      ```properties
      spring.data.mongodb.uri=mongodb://localhost:27017/exchangeXP
      ```
4. **Install dependencies**
   ```bash
   mvn clean install
   ```
5. **Run the application**
   ```bash
   mvn spring-boot:run
   ```
   The backend will start at **http://localhost:8080** (unless you changed the port).

---

## 📁 Folder Structure

```
ExchangeXP/
├── src/
│   ├── main/
│   │   ├── java/com/exchangexp/
│   │   │   ├── controller/        # REST Controllers
│   │   │   ├── service/           # Business logic
│   │   │   ├── repository/        # MongoDB repositories
│   │   │   ├── model/             # Entity classes
│   │   │   └── config/            # Security & App Config
│   │   └── resources/
│   │       ├── application.properties
│   │       └── static/
│   └── test/                      # Unit & integration tests
├── pom.xml
└── README.md
```

---

## 📄 API Endpoints

| HTTP Method | Endpoint                       | Description                                             |
|--------------|--------------------------------|---------------------------------------------------------|
| `POST`       | `/public/create-user`          | Registers a new user                                    |
| `GET`        | `/public/health-check`         | Checks the health of the application                    |
| `POST`       | `/blog/post/{username}`        | Creates a new blog post                                 |
| `GET`        | `/blog/findAll/{username}`     | Retrieves all blogs by a user                           |
| `GET`        | `/blog/find/{username}/{id}`   | Retrieves a specific blog post by ID                    |
| `PUT`        | `/blog/update/{username}/{id}` | Updates a specific blog post                            |
| `DELETE`     | `/blog/delete/{username}/{id}` | Deletes a blog post                                     |
| `POST`       | `/user/update`                 | Updates the current user's information                  |
| `DELETE`     | `/user/delete`                 | Deletes the current authenticated user                  |
| `GET`        | `/admin/all-users`             | (Admin) Retrieves all users                             |
| `GET`        | `/admin/all-blogs`             | (Admin) Retrieves all blog posts                        |

---

## 🤝 Contributing

**ExchangeXP** is currently under active development.  
External contributions are not yet open, but will be welcomed in the future once the initial phase is complete.

Stay tuned and ⭐ the repo to follow updates!

---

## 📜 License

Distributed under the **MIT License**.  
See the [`LICENSE`](./LICENSE) file for more information.

---

## 📧 Contact

**Md Jobaer Islam Alif**  
📩 [alifjobaer12@gmail.com](mailto:alifjobaer12@gmail.com)  
🔗 [GitHub Repository](https://github.com/alifjobaer12/ExchangeXP)

---

> _“Code. Create. Connect.” — The spirit of ExchangeXP._
