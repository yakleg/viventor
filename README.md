Java Developer coding task

Coding challenge task is to create backend for a simple "banking" application:
* Client should be able to sign up with email & password
* Client should be able to deposit money
* Client should be able to withdraw money
* Client should be able to see account balance and statement

You should create database structure, backend and REST API.

Technology stack
* Java 8
* Spring Boot
* PostgreSQL or any in-memory database (like H2/HSQLDB)
* Maven or Gradle as build system
* Host your code on GitHub/Bitbucket/GitLab public repo

Note: Spring security usage is not necessary.

What we will take into consideration on final valuation:
- How well business requirements are understood
- Quality of the code
- Test coverage

Plan:

/api/token - {POST, DELETE}

/api/users - {GET, POST}

/api/users/{userId} - {GET, DELETE}

/api/users/{userId}/accounts - {GET, POST}

/api/users/{userId}/accounts/{accountId} - {GET, DELETE}

/api/users/{userId}/accounts/{accountId}/balance - {GET}

/api/users/{userId}/accounts/{accountId}/postings - {GET, POST}

