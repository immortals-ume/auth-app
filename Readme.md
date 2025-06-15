# Auth App

## Overview

Auth App is a robust authentication and authorization service designed to provide secure access control for
applications. It includes various authentication mechanisms, token management, and third-party integration for a
seamless authentication experience.

## Features

### 1. Username/Password Authentication

- Secure login using email/username and password.
- Password hashing and storage using industry best practices.
- Role-based access control for different user privileges.

### 8. Role-Based Access Control (RBAC)

- Fine-grained access control based on user roles and permissions.
- Custom role definitions for flexibility.

### 3. OTP Service (Mobile/Email)

- One-time password (OTP) generation and validation for enhanced security.
- Support for SMS and email OTP delivery.

### 4. Two-Factor Authentication (2FA)

- Additional layer of security using OTP or app-based authenticators (TOTP).
- Optional enforcement of 2FA for critical actions.

### 10. Remember Me

- Persistent login functionality for a better user experience.
- Secure storage of authentication tokens with expiration control.

### 5. Refresh Token

- Secure refresh token mechanism to extend user sessions without re-authentication.
- Expired token handling and revocation support.

### 7. Anonymous Authentication

- Support for guest/anonymous users with limited access.
- Upgrade from anonymous to registered user without losing session data.

### 6. Logout

- Secure user logout with token invalidation.
- Global logout for multiple active sessions.

## Installation

1. Clone the repository:
   ```sh
   git clone https://github.com/your-repo/auth-app.git
   cd auth-app
   ```
2. Install dependencies:
   ```sh
   mvn clean install 
   ```
3. Configure environment variables in `.env` or `application.properties` or `application.yml`.
4. Run the application:
   ```sh
   mvn spring-boot:run
   ```

## API Endpoints

| Method | Endpoint         | Description                    |
|--------|------------------|--------------------------------|
| POST   | /auth/login      | User login                     |
| POST   | /auth/register   | User registration              |
| POST   | /auth/logout     | User logout                    |
| POST   | /auth/refresh    | Token refresh                  |
| POST   | /auth/otp        | Generate OTP                   |
| POST   | /auth/verify-otp | Verify OTP                     |
| GET    | /auth/oauth2     | OAuth2 authentication          |
| GET    | /auth/user       | Get authenticated user details |

## Roles and privileges - (Sample Privileges and Roles for the APP these have to be changed as per the requirement )

| id | privileges             | Description                                        | country |
|----|------------------------|----------------------------------------------------|---------|
| 1  | READ_ALL_PRIVILEGE     | read all privilege   (Read any data in the system) |         |
| 2  | READ_PRIVILEGE         | (read single/collection of selected entity)        |         |
| 3  | WRITE_INSERT_PRIVILEGE | write insert Privilege  (create a new entity)      |         |
| 4  | WRITE_UPDATE_PRIVILEGE | write update Privilege  (update a existing entity) |         |
| 5  | DELETE_PRIVILEGE       | Delete Privilege   (SOFT delete a existing entity) |         |
| 6  | ALL                    | Admin Privileges   ( can do  any of the above)     |         |
| 7  | EXPORT_PRIVILEGE       | exports data privilege                             |         |
| 8  | IMPORT_PRIVILEGE       | imports data privilege                             |         |
| 9  | COMMUNICATION_SMS      |                                                    | IN      |
| 10 | COMMUNICATION_WHATSAPP |                                                    | IN      |
| 11 | COMMUNICATION_EMAIL    |                                                    | IN      |
| 12 | COMMUNICATION_BANNER   |                                                    | IN      |
| 13 | ACCESS_STORAGE         |                                                    | IN      |

| Roles  | privileges | Description               |
|--------|------------|---------------------------|
| ADMIN  | ALL        | Admin have all privileges |
| USER_2 |            |                           |
| USER_1 |            |                           |
| SME    |            |                           |
| EM     |            |                           |
| AME      |            |                           |


## Security Measures

- Password hashing with **BCrypt**.
- JWT-based authentication.
- Secure HTTPS communication.
- Rate limiting and brute force protection.

## License

This project is licensed under the MIT License.

## Contributing

Feel free to contribute by submitting issues or pull requests. Follow the coding standards and best practices while
contributing.

## Contact

For inquiries, contact **immortals-ume** at **immortals-ume@gmail.com**.

---

*Happy Coding! 🚀*

