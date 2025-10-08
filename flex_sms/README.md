# Flex SMS Service

This service provides SMS and email notification capabilities with payment processing integration.

## Features

- Send SMS and email notifications
- Process payments through an external payment service
- Retry mechanism for failed payment operations
- Configurable notification methods (SMS, Email, or both)

## Prerequisites

- Java 21
- Maven
- RabbitMQ
- PostgreSQL
- External Payment Service

## Configuration

### Environment Variables

Create a `.env` file in the project root with the following variables:

```env
# Application
SERVER_PORT=8080

# Database
DATASOURCE_URL=jdbc:postgresql://localhost:5432/flex_sms
DATASOURCE_USERNAME=postgres
DATASOURCE_PASSWORD=postgres

# RabbitMQ
RABBITMQ_HOST=localhost
RABBITMQ_PORT=5672
RABBITMQ_USERNAME=guest
RABBITMQ_PASSWORD=guest

# Payment Service
PAYMENT_SERVICE_URL=http://payment-service:8080
PAYMENT_SERVICE_API_KEY=your-api-key

# Email (SMTP)
SPRING_MAIL_HOST=smtp.example.com
SPRING_MAIL_PORT=587
SPRING_MAIL_USERNAME=your-email@example.com
SPRING_MAIL_PASSWORD=your-email-password
```

### Application Properties

Key properties in `application.properties`:

```properties
# Notification Configuration
notification.preferred-method=EMAIL  # Options: EMAIL, SMS, or BOTH

# Retry Configuration
spring.retry.max-attempts=3
spring.retry.backoff.initial-interval=1000
spring.retry.backoff.multiplier=2.0

# File Upload
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB
```

## API Endpoints

### Process Payment

```http
POST /api/payments/process
Content-Type: application/json

{
  "customerId": "CUST12345",
  "customerName": "John Doe",
  "customerPhone": "+255712345678",
  "customerEmail": "john@example.com",
  "accountNumber": "ACC789012",
  "amount": 150000.00,
  "currency": "TZS",
  "paymentMethod": "MOBILE_MONEY",
  "paymentReference": "INV-2025-001",
  "description": "Monthly subscription payment"
}
```

### Check Payment Status

```http
GET /api/payments/{transactionId}/status
```

### Process Refund

```http
POST /api/payments/{transactionId}/refund
Content-Type: application/json

{
  "amount": 50000.00,
  "reason": "Partial refund for overpayment"
}
```

## Building and Running

1. Build the application:
   ```bash
   ./mvnw clean package
   ```

2. Run the application:
   ```bash
   java -jar target/flex-sms-0.0.1-SNAPSHOT.jar
   ```

## Testing

Run the tests:
```bash
./mvnw test
```

## Monitoring

The application exposes the following endpoints for monitoring:

- Health: `GET /actuator/health`
- Metrics: `GET /actuator/metrics`
- Retry Stats: `GET /actuator/retryevents`

## Logging

Logs are configured to output to both console and file (`logs/application.log`).

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
