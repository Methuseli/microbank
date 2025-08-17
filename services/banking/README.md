# Banking Service - Account & Transaction Management

The Banking Service handles all banking operations including account management, transactions, and financial data processing in the SecureBank microservice platform. Built with Spring Boot WebFlux for high-performance, reactive transaction processing.

## 🏗️ Service Architecture

This service follows a reactive, event-driven architecture:

```
src/main/java/com/microbank/banking/
├── controllers/          # REST API endpoints
├── services/            # Business logic layer
├── repository/          # Data access layer (R2DBC)
├── entity/              # Domain models
├── dto/                 # Data transfer objects
└── config/              # Configuration classes
```

## 🚀 Why WebFlux for Banking Operations?

### 1. **High-Performance Transaction Processing**
- Banking operations require handling thousands of concurrent transactions
- WebFlux's non-blocking I/O prevents thread starvation during peak transaction volumes
- Event-loop model provides consistent performance under load

### 2. **Real-time Transaction Updates**
- Reactive streams enable real-time balance updates
- Non-blocking database operations for faster transaction processing
- Efficient handling of concurrent deposit/withdrawal operations

### 3. **Event-Driven Account Creation**
- Reactive Kafka consumer for processing user-created events
- Non-blocking account creation when users register
- Automatic retry mechanisms with backpressure handling

### 4. **Database Efficiency**
- R2DBC provides reactive database access for PostgreSQL
- Connection pooling optimized for reactive workloads
- Better resource utilization for financial data queries

### 5. **Scalability for Financial Operations**
- Lower memory footprint per transaction
- Better horizontal scaling capabilities
- Ideal for cloud-native banking deployments

## 🛠️ Technology Stack

- **Spring Boot 3.5.4** with **WebFlux**
- **Spring Security** (Reactive JWT validation)
- **R2DBC PostgreSQL** (Reactive Database Access)
- **Reactor Kafka** (Event Consumption)
- **Spring Data R2DBC** (Reactive Data Access)
- **Lombok** (Code Generation)

## 📊 Core Features

### Account Management
- Automatic account creation via Kafka events
- Unique account number generation
- Account balance management
- Account holder validation

### Transaction Processing
- Deposit operations with validation
- Withdrawal operations with insufficient funds checking
- Transaction history tracking
- Real-time balance updates

### Event-Driven Integration
- Consumes "user-created" events from Client Service
- Automatic bank account creation for new users
- Reactive event processing with error handling

## 🔌 API Endpoints

### Account Management
```http
POST /api/v1/bank-accounts
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "accountHolderId": "user-uuid",
  "balance": 0.0
}
```

```http
GET /api/v1/bank-accounts/{userId}
Authorization: Bearer <jwt-token>
```

```http
GET /api/v1/bank-accounts/{id}/details
Authorization: Bearer <jwt-token>
```

```http
DELETE /api/v1/bank-accounts/{id}
Authorization: Bearer <jwt-token>
```

### Transaction Operations
```http
PATCH /api/v1/bank-accounts/{id}/deposit
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "amount": 100.50,
  "description": "Salary deposit"
}
```

```http
PATCH /api/v1/bank-accounts/{id}/withdraw
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "amount": 50.25,
  "description": "ATM withdrawal"
}
```

### Transaction History
```http
GET /api/v1/bank-accounts/{accountId}/transactions
Authorization: Bearer <jwt-token>
```

## 🗄️ Database Schema

```sql
-- Bank accounts table
CREATE TABLE bank_accounts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_holder_id UUID NOT NULL,
    account_number VARCHAR(20) NOT NULL UNIQUE,
    balance DECIMAL(15, 2) DEFAULT 0.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT
);

-- Bank transactions table
CREATE TABLE bank_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_id UUID NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    transaction_type VARCHAR(20) NOT NULL CHECK (transaction_type IN ('deposit', 'withdrawal')),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    description TEXT,
    FOREIGN KEY (account_id) REFERENCES bank_accounts(id) ON DELETE CASCADE
);
```

## 🏃‍♂️ Running the Service

### Prerequisites
- Java 21+
- PostgreSQL (or Docker)
- Apache Kafka (or Docker)

### Development Mode
```bash
# Start dependencies
docker-compose up banking_db kafka

# Set environment variables
export SPRING_R2DBC_URL=r2dbc:postgresql://localhost:5433/banking_db
export SPRING_R2DBC_USERNAME=banking_user
export SPRING_R2DBC_PASSWORD=banking_password
export SPRING_JWT_SECRET=your-secret-key
export SPRING_KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Run the service
./mvnw spring-boot:run
```

### Production Mode
```bash
# Build the application
./mvnw clean package

# Run with production profile
java -jar target/banking-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Docker
```bash
# Build Docker image
docker build -t microbank/banking-service .

# Run container
docker run -p 8080:8080 \
  -e SPRING_R2DBC_URL=r2dbc:postgresql://host:5432/banking_db \
  -e SPRING_R2DBC_USERNAME=banking_user \
  -e SPRING_R2DBC_PASSWORD=banking_password \
  -e SPRING_JWT_SECRET=your-secret-key \
  -e SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:9092 \
  microbank/banking-service
```

## 🧪 Testing

### Unit Tests
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=BankAccountServiceImplTest

# Run tests with coverage
./mvnw test jacoco:report
```

### Integration Tests
```bash
# Run integration tests
./mvnw test -Dtest=*IntegrationTest

# Test with embedded database
./mvnw test -Dspring.profiles.active=test
```

### Load Testing
```bash
# Use Apache Bench for transaction load testing
ab -n 1000 -c 10 -H "Authorization: Bearer <token>" \
   -H "Content-Type: application/json" \
   -p deposit.json \
   http://localhost:8080/api/v1/bank-accounts/{id}/deposit
```

## ⚙️ Configuration

### Application Properties
```yaml
spring:
  application:
    name: banking
  r2dbc:
    url: r2dbc:postgresql://banking_db/${SPRING_R2DBC_URL}
    username: ${SPRING_R2DBC_USERNAME}
    password: ${SPRING_R2DBC_PASSWORD}
  kafka:
    bootstrap-servers: ${SPRING_KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
    consumer:
      group-id: banking-group
      auto-offset-reset: earliest
      properties:
        isolation.level: read_committed

jwt:
  secret: ${SPRING_JWT_SECRET}
  expiration: 3600000

server:
  port: 8080
```

### Environment Variables
- `SPRING_R2DBC_URL`: Database connection URL
- `SPRING_R2DBC_USERNAME`: Database username
- `SPRING_R2DBC_PASSWORD`: Database password
- `SPRING_JWT_SECRET`: JWT validation secret
- `SPRING_KAFKA_BOOTSTRAP_SERVERS`: Kafka broker addresses

## 🔐 Security Features

### JWT Token Validation
- Validates JWT tokens from Client Service
- Extracts user information from token claims
- Role-based access control for admin operations

### Transaction Security
- Validates account ownership before operations
- Prevents negative balance withdrawals
- Audit trail for all transactions

### Data Protection
- Encrypted database connections
- Secure handling of financial data
- Input validation and sanitization

## 📈 Business Logic

### Account Number Generation
```java
private String generateRandomAccountNumber() {
    long min = (long) Math.pow(10, ACCOUNT_NUMBER_LENGTH - 1);
    long max = (long) Math.pow(10, ACCOUNT_NUMBER_LENGTH) - 1;
    long randomNumber = ThreadLocalRandom.current().nextLong(min, max + 1);
    return String.valueOf(randomNumber);
}
```

### Transaction Processing
```java
public Mono<BankAccount> deposit(UUID id, double amount, String description) {
    return bankAccountRepository.findById(id)
        .flatMap(account -> {
            account.setBalance(account.getBalance() + amount);
            // Save transaction record asynchronously
            bankTransactionRepository.save(
                BankTransaction.builder()
                    .accountId(id)
                    .amount(amount)
                    .transactionType("deposit")
                    .description(description)
                    .build()
            ).subscribe();
            return bankAccountRepository.save(account);
        });
}
```

### Event-Driven Account Creation
```java
@PostConstruct
public void startConsuming() {
    consumerDisposable = kafkaReceiver.receive()
        .concatMap(record -> {
            UUID userId = UUID.fromString(record.value());
            return createAccountForUser(userId)
                .then(commitOffset(record.receiverOffset()));
        })
        .retryWhen(Retry.backoff(10, Duration.ofSeconds(1)))
        .subscribe();
}
```

## 🔄 Event Processing

### User Created Event Consumer
The service automatically creates bank accounts when it receives user-created events:

```json
{
  "topic": "user-created",
  "key": "user-uuid",
  "value": "user-uuid"
}
```

### Event Processing Flow
1. Consume user-created event from Kafka
2. Generate unique account number
3. Create bank account with zero balance
4. Commit Kafka offset
5. Handle errors with retry mechanism

## 📊 Monitoring & Observability

### Health Checks
```http
GET /actuator/health
```

### Metrics
- Transaction processing rates
- Account creation metrics
- Database connection pool status
- Kafka consumer lag

### Application Logs
```bash
# View transaction logs
./mvnw spring-boot:run | grep "Transaction"

# View Kafka consumer logs
./mvnw spring-boot:run | grep "Kafka"
```

## 🚨 Error Handling

### Transaction Errors
- **Insufficient Funds**: Prevents overdrafts
- **Account Not Found**: Validates account existence
- **Invalid Amount**: Ensures positive transaction amounts

### Event Processing Errors
- **Retry Mechanism**: Automatic retry with exponential backoff
- **Dead Letter Queue**: Failed events for manual processing
- **Offset Management**: Proper Kafka offset handling

### Database Errors
- **Connection Failures**: Automatic reconnection
- **Constraint Violations**: Proper error responses
- **Transaction Rollbacks**: Data consistency protection

## 🎯 Performance Optimizations

### Database Optimizations
- Connection pooling for R2DBC
- Indexed queries for account lookups
- Batch processing for transaction history

### Reactive Optimizations
- Non-blocking I/O operations
- Backpressure handling
- Efficient memory usage

### Caching Strategy
- Account balance caching
- Transaction history pagination
- Optimized query patterns

## 🛠️ Development Guidelines

### Reactive Programming Best Practices
- Use `Mono` for single values
- Use `Flux` for multiple values
- Implement proper error handling
- Avoid blocking operations

### Transaction Safety
- Ensure atomic operations
- Implement proper validation
- Handle concurrent access
- Maintain data consistency

### Testing Strategy
- Unit tests for business logic
- Integration tests for database operations
- Contract tests for API endpoints
- Load tests for performance validation
