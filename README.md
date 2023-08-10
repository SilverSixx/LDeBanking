# LDeBanking - Banking Application

LDeBanking is a secure and feature-rich banking web-based built using Spring Boot for backend, along with security and verification functionalities. This application allows users to perform various banking operations securely and conveniently.

## Features

- Form-based login.
- User Authentication/Authorization: Secure user registration and login process to access banking services.
- Account Management: Create, view, and manage user accounts with transaction history.
- Transference: Transfer funds between different user accounts within the system.
- OTP verification in any transaction.
- Transaction History: View a detailed history of transactions for each account.
- Email Notifications: Send email notifications for various account activities, such as successful transactions, password resets, etc.
- Security: Implement robust security measures to protect user data and prevent unauthorized access.

## Technologies Used

- Spring Boot: Backend framework to develop the application efficiently.
- Spring Security: Ensure secure authentication and authorization of users.
- Spring Data JPA: Simplify data access and manipulation with JPA.
- Hibernate: ORM tool for database mapping and management.
- Spring Mail: Integration for sending email notifications.
- Twilio: Integration for sending otp verifications.
- PostgreSQL: Relational database management system for data storage.
- Itextpdf: Used for generating bank statement.
- Reactor-core: Improvement of non-blocking feature.
- JWT: Implemented in authentication and authorization.
- Maven: Build and dependency management tool.

## Setup Instructions

1. Clone the repository to your local machine.

```bash
git clone https://github.com/your-username/LDeBanking.git
```
2. Set up a PostSQL database and configure the database connection in the `application.properties` file.

3. Run the application using Maven.

```bash
cd LDeBanking
mvn spring-boot:run
```
4. Access the application in your web browser at `http://localhost:8080`.

## Usage

1. Register as a new user with valid credentials.
2. Log in to your account using the registered credentials.
3. Easily credit/debit with otp verifications.
4. Use the “Fund Transfer” feature to transfer money to other accounts.
5. Check your transaction history for a record of all past transactions.
6. Receive email notifications for critical account activities.

## Contributing

Contributions to LDeBanking are welcome! If you have any suggestions, bug reports, or new features to add, please feel free to open an issue or submit a pull request.

## License

This project is licensed under the MIT License. Feel free to use, modify, and distribute the code as per the terms of the license.

## Contact

If you have any questions or need support, please contact at:

Email: phamluongdat231103@gmail.com

Thank you for choosing LDeBanking! We hope you enjoy using our secure and reliable banking application.
