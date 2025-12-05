# StartUp Forum Web App

## Overview

StartUp is a community-driven forum designed for entrepreneurs, startup founders, and business enthusiasts.
The platform enables users to share ideas, validate concepts, receive feedback, and engage with others in the world of tech, fintech, AI, SaaS, and general business innovation.

It includes role-based access control, content moderation, post sharing, and a clean UI for effortless browsing.

## Features

**User Management**

- User registration and login
- Role-based access (ADMIN, USER)
- Admin-only user browsing and management (blocking, unblocking, promoting, deleting violent posts/comments)
- Profile editing

**Posts & comments**

- Create, edit, delete and like posts
- Create, reply, edit and delete comments under posts

**Searching & Filtering**

- Admins can filter users by their username, email, or first name
- Users can use a global search and filter posts based on title, content or creator

**Security**

- Spring Security + JWT
- Password Hashing (BCrypt)

## Tech Stack

**Backend**

- Java 17+
- Spring Boot / Spring Web
- Spring Security / Spring Data JPA
- MariaDB
- Hibernate

**Frontend**

- Vue.js
- TailwindCSS
- Vite

**IDE & Tools**

- IntelliJ IDEA
- Git & GitHub
- Docker

## Installation

Follow these steps to set up and run the application:

- First, download the project 

```bash
    git clone https://github.com/forum-startup/forum-startup
```

- Install node packages

```bash
    cd ./frontend
    npm install
```

- Run the frontend development server, it will host on http://localhost:5173

```bash
    npm run dev
```

- Then setup the backend starting with the application.properties file

```bash
    cd ../backend/src/main/resources
    nano application.properties
```

- Paste the following template and fill in your values

```bash
    spring.application.name=forum-startup

    spring.datasource.url=jdbc:mariadb://localhost:{{your_db_port}}/{{your_db_name}}
    spring.datasource.username={{your_db_username}}
    spring.datasource.password={{your_db_password}}
    spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true

    jwt.secret={{your_jwt_secret}}
```

- You can generate your JWT secret here: https://jwtgenerator.com, and if you are about to play with the app make sure not to commit the application.properties file since it contains the secret (or you can move to a different .properties file and exclude it in your .gitignore).

- You can also make use of the create.sql for the database schema and the insert.sql for an initial insert. Both .sql files you can find inside forum-startup/backend/db

- Then run the backend from the IDE of your choice and you are all set.

- Now simply open your browser on http://localhost:5173, sign up and become part of our community!

## Web App Demo

![Demo](./demo/demo.gif)

## Database Diagram

<img src="/backend/db/diagram.png" alt="Database Diagram" height="600" width="600" />

## Contributors
For further information, please feel free to contact us:

| Authors              | Emails    | GitHub|
| ------               | ------    |------ |
| Ivelin Yanev         | ivelinyanev00@gmail.com     | https://github.com/ivelinyanev  |
| Martin Dimitrov         | dimitmarto@gmail.com     | https://github.com/marto-dim  |
| Mihail Angelov        |  mihailangelov@gmail.com    | https://github.com/Misho1089  |

## License

[MIT](https://choosealicense.com/licenses/mit/)
