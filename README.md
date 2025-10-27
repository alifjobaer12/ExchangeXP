# ExchangeXP

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![MongoDB](https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white)

This repository contains the backend for **ExchangeXP**, a team project built using the Spring Boot framework and MongoDB.

## 🌟 About The Project

[//]: # (&#40;***Note:** Add a more detailed description of your project here. What problem does it solve? What is its main purpose?*&#41;)

This project serves as the core backend API for the ExchangeXP application, handling all business logic, data processing, and database interactions with a MongoDB database.

---

## 🛠️ Technologies Used

This project is built with:

* **[Java](https://www.java.com/)**: The core programming language.
* **[Spring Boot](https://spring.io/projects/spring-boot)**: The main application framework.
* **[Spring Data MongoDB](https://spring.io/projects/spring-data-mongodb)**: For data persistence and interaction with the MongoDB database.
* **[Spring Security](https://spring.io/projects/spring-security)**: (Optional: Add if you are using it) For handling authentication and authorization.
* **[Maven](https://maven.apache.org/)**: For project dependency management and build automation.

---

## ✨ Features

*(List the main features of your application based on your endpoints. Here are some examples.)*

* User registration and management
* Blog post creation, retrieval, updating, and deletion (CRUD)
* Administrative endpoints for viewing all users and blogs
* Application health check status

---

## 🚀 Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

You will need the following software installed on your machine:

* JDK (Java Development Kit) 17 or higher
* Apache Maven
* MongoDB (A local instance or a connection string to a cloud database like MongoDB Atlas)

### Installation & Setup

1.  **Clone the repository:**
    ```sh
    git clone [https://github.com/alifjobaer12/ExchangeXP.git](https://github.com/alifjobaer12/ExchangeXP.git)
    ```
2.  **Navigate to the project directory:**
    ```sh
    cd ExchangeXP
    ```
3.  **Configure the database:**
    * Open `src/main/resources/application.properties` (or `application.yml`).
    * Update the `spring.data.mongodb.uri` property with your MongoDB connection string.
      (e.g., `spring.data.mongodb.uri=mongodb://localhost:27017/exchangeXP`)
4.  **Install dependencies:**
    ```sh
    mvn clean install
    ```
5.  **Run the application:**
    ```sh
    mvn spring-boot:run
    ```
    The application will start on `http://localhost:8080` (or the port you configured).

---

## 📄 API Endpoints

Here is a list of the available API endpoints for the service:

| HTTP Method | Endpoint                       | Description                                             |
|:------------|:-------------------------------|:--------------------------------------------------------|
| `POST`      | `/public/create-user`          | Registers a new user.                                   |
| `GET`       | `/public/health-check`         | Checks the health of the application.                   |
| `DELETE`    | `/blog/delete/{username}/{id}` | Deletes a specific blog post by ID for a user.          |
| `GET`       | `/blog/find/{username}/{id}`   | Finds a specific blog post by ID for a user.            |
| `GET`       | `/blog/findAll/{username}`     | Finds all blog posts for a specific user.               |
| `POST`      | `/blog/post/{username}`        | Creates a new blog post for a user.                     |
| `PUT`       | `/blog/update/{username}/{id}` | Updates a specific blog post by ID for a user.          |
| * `DELETE`  | `/user/delete`                 | Deletes the currently authenticated user.               |
| `POST`      | `/user/update`                 | Updates the currently authenticated user's information. |
| `GET`       | `/admin/all-blogs`             | (Admin) Retrieves all blog posts from all users.        |
| `GET`       | `/admin/all-users`             | (Admin) Retrieves all users in the system.              |

---

## 🤝 Contributing

**This project is currently under active development.**

At this moment, we are not accepting external contributions. Once the initial development phase is complete, this project will be open to contributions from the community.

We appreciate your interest and encourage you to check back in the future!

---

## 📜 License

Distributed under the MIT License. See `LICENSE` file for more information. (***Note:** You will need to add a `LICENSE` file to your repository.)*

---

## 📧 Contact

Md Jobaer Islam Alif - [alifjobaer12@gmail.com](mailto:alifjobaer12@example.com)

Project Link: [https://github.com/alifjobaer12/ExchangeXP](https://github.com/alifjobaer12/ExchangeXP)