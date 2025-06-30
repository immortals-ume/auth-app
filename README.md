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

- implemented in a separate service called OTP Service (GITHUB LINK TO BE ADDED)
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
   git clone https://github.com/immortals-ume/auth-app.git
   cd auth-app
   ```
2. Install dependencies:
   ```sh
   mvn clean install 
   ```
3. Configure environment variables in `.env` or `application.yml`.

4. Run the application:
   ```sh
   mvn spring-boot:run
   ```
## Security Measures

- JWT-based authentication.
- Secure HTTPS communication.
- Rate limiting and brute force protection.

## License

This project is licensed under the MIT License.

## Contributing

Feel free to contribute by submitting issues or pull requests. Follow the coding standards and best practices while
contributing.

## Contact

For inquiries, contact **immortals-ume** at **srivastavakapil34@gmail.com**.

---

*Happy Coding! 🚀*

