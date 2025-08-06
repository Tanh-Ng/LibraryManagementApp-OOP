# LibraryManagementApp-OOP

**School Project Notice:**

This project was developed as part of a school assignment to help students become familiar with object-oriented programming (OOP) concepts and common software design patterns. The application demonstrates the use of OOP principles such as encapsulation, inheritance, and polymorphism, as well as the application of design patterns in a real-world context.

A JavaFX-based Library Management System that allows users and administrators to manage books, users, and borrowing activities. Built with Java 22, JavaFX, and MySQL, following object-oriented programming principles.

## Documentation

- [Features](Feature.md)
- [Database Design](DatabaseDesign.md)

// ... existing code ...entication (login)
- User dashboard for browsing and searching books
- Book details and categorization
- Borrow and return management
- Admin dashboard for managing users, books, and borrow records
- Responsive UI with JavaFX and FXML
- Persistent storage with MySQL database

## Project Structure

```
LibraryManagementApp-OOP/
├── src/
│   ├── main/
│   │   ├── java/com/librarymanagement/...
│   │   └── resources/
│   │       ├── FXML/ (UI layouts)
│   │       ├── css/ (stylesheets)
│   │       ├── icons/ (icons/images)
│   │       └── db.properties (database config)
│   └── test/
│       └── java/com/librarymanagement/...
├── pom.xml
└── README.md
```

## Prerequisites

- Java 22 or higher
- Maven
- MySQL Server

## Setup Instructions

1. **Clone the repository:**
   ```sh
   git clone <repo-url>
   cd LibraryManagementApp-OOP
   ```

2. **Configure the Database:**
   - Create a MySQL database named `libraryappdb`.
   - Update `src/main/resources/db.properties` with your MySQL credentials:
     ```properties
     db.url=jdbc:mysql://<host>/<database>
     db.username=<your-username>
     db.password=<your-password>
     ```
   - Import the required schema and initial data (if provided).

3. **Build the Project:**
   ```sh
   mvn clean install
   ```

4. **Run the Application:**
   ```sh
   mvn javafx:run
   ```
   Or run the `LibraryManagementApp` main class from your IDE.

## Running Tests

- Unit tests are located in `src/test/java/com/librarymanagement/`.
- To run all tests:
  ```sh
  mvn test
  ```

## Main Technologies Used

- Java 22
- JavaFX 23
- MySQL
- Maven
- JUnit 4/5

## UI Structure

- FXML files for user and admin screens are in `src/main/resources/FXML/`
- Stylesheets are in `src/main/resources/css/`

## Authors

- [Nguyen The Anh](https://github.com/Tanh-Ng)
- [Dang Quang Huy](https://github.com/HuyDang05)
- [Hoang Gia Bao](https://github.com/Hoanggiabao1)
- [Do Chi Long](https://github.com/ryuuch1)

## License

This project is for educational purposes.
