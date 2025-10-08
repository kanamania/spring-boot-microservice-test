\# Flex SMS Service



This service provides SMS and email notification capabilities with payment processing integration.



\## Features



\- Send SMS and email notifications

\- Process payments through an external payment service

\- Retry mechanism for failed payment operations

\- Configurable notification methods (SMS, Email, or both)



\## Prerequisites



\- Java 21

\- Maven

\- RabbitMQ

\- PostgreSQL

\- External Payment Service



\## Configuration



\### Environment Variables



Create a `.env` file in the project root with the following variables:



```env

\# Application

SERVER\_PORT=8080



\# Database

DATASOURCE\_URL=jdbc:postgresql://localhost:5432/flex\_sms

DATASOURCE\_USERNAME=postgres

DATASOURCE\_PASSWORD=postgres



\# RabbitMQ

RABBITMQ\_HOST=localhost

RABBITMQ\_PORT=5672

RABBITMQ\_USERNAME=guest

RABBITMQ\_PASSWORD=guest



\# Payment Service

PAYMENT\_SERVICE\_URL=http://payment-service:8080

PAYMENT\_SERVICE\_API\_KEY=your-api-key



\# Email (SMTP)

SPRING\_MAIL\_HOST=smtp.example.com

SPRING\_MAIL\_PORT=587

SPRING\_MAIL\_USERNAME=your-email@example.com

SPRING\_MAIL\_PASSWORD=your-email-password

```



\### Application Properties



Key properties in `application.properties`:



```properties

\# Notification Configuration

notification.preferred-method=EMAIL  # Options: EMAIL, SMS, or BOTH



\# Retry Configuration

spring.retry.max-attempts=3

spring.retry.backoff.initial-interval=1000

spring.retry.backoff.multiplier=2.0



\# File Upload

spring.servlet.multipart.max-file-size=10MB

spring.servlet.multipart.max-request-size=10MB

```



\## API Endpoints



\### Process Payment



```http

POST /api/payments/process

Content-Type: application/json



{

&nbsp; "customerId": "CUST12345",

&nbsp; "customerName": "John Doe",

&nbsp; "customerPhone": "+255712345678",

&nbsp; "customerEmail": "john@example.com",

&nbsp; "accountNumber": "ACC789012",

&nbsp; "amount": 150000.00,

&nbsp; "currency": "TZS",

&nbsp; "paymentMethod": "MOBILE\_MONEY",

&nbsp; "paymentReference": "INV-2025-001",

&nbsp; "description": "Monthly subscription payment"

}

```



\### Check Payment Status



```http

GET /api/payments/{transactionId}/status

```



\### Process Refund



```http

POST /api/payments/{transactionId}/refund

Content-Type: application/json



{

&nbsp; "amount": 50000.00,

&nbsp; "reason": "Partial refund for overpayment"

}

```



\## Building and Running



1\. Build the application:

&nbsp;  ```bash

&nbsp;  ./mvnw clean package

&nbsp;  ```



2\. Run the application:

&nbsp;  ```bash

&nbsp;  java -jar target/flex-sms-0.0.1-SNAPSHOT.jar

&nbsp;  ```



\## Testing



Run the tests:

```bash

./mvnw test

```



\## Monitoring



The application exposes the following endpoints for monitoring:



\- Health: `GET /actuator/health`

\- Metrics: `GET /actuator/metrics`

\- Retry Stats: `GET /actuator/retryevents`



\## Logging



Logs are configured to output to both console and file (`logs/application.log`).



\## License



This project is licensed under the MIT License - see the \[LICENSE](LICENSE) file for details.



