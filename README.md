# 💌 UsForever: A Digital Love Capsule

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)
![Vue.js](https://img.shields.io/badge/Vue.js-35495E?style=for-the-badge&logo=vuedotjs&logoColor=4FC08D)

**UsForever** is a comprehensive full-stack web application designed for couples to preserve their history. Unlike simple static sites, this application uses a robust relational database and a secure backend API to manage photos, videos, journal entries, and calendar events.

## ✨ Features

### 🌸 The Frontend (Vue 3)
-   **Immersive UI:** A soft, romantic aesthetic with floating animations and curated typography.
-   **Media Gallery:** Hybrid support for photos and videos using Cloudinary.
    -   *Features:* Hover-to-play videos, masonry grid, category filtering.
-   **Components:** Custom Lightbox, Toast Notifications, and Audio Player.
-   **SPA Routing:** Seamless transitions between the Gallery, Notes, and Calendar.

### ⚙️ The Backend (Spring Boot)
-   **RESTful API:** Endpoints for creating, reading, and deleting memories and notes.
-   **Data Persistence:** Uses **Spring Data JPA** (Hibernate) to map entities to **PostgreSQL**.
-   **Error Handling:** Global exception handling for smooth API responses.
-   **Service Layer:** Business logic separation for managing media metadata.

### 🗄️ Database (PostgreSQL)
-   Structured tables for `Memories`, `Notes`, and `ImportantDates`.
-   Stores metadata (Cloudinary URLs, text content, timestamps, categories).

---

## 🛠️ System Architecture

```mermaid
graph LR
    A[Vue 3 Client] -- REST API / JSON --> B[Spring Boot Backend]
    B -- JDBC --> C[(PostgreSQL DB)]
    A -- Uploads/Retrieval --> D[Cloudinary Storage]
    B -- Stores Metadata --> C
