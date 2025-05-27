# LSF Server Spring Boot Migration

This project is a migration of the existing AKKA-based LSF server to Spring Boot.

## Project Structure

- Controllers: Replace AKKA consumers to handle HTTP requests
- Services: Replace AKKA processors to handle business logic
- Configuration: Set up Spring Boot components
- Models: Data models transferred from the original project

## Migration Approach

1. Create a parallel Spring Boot implementation
2. Maintain the same API endpoints and message formats
3. Gradually migrate functionality from AKKA to Spring Boot
4. Perform thorough testing to ensure compatibility
5. Switch over when ready

## Running the Application

```bash
# Build the application
./gradlew build

# Run the application
./gradlew bootRun
```

## Testing

```bash
# Run tests
./gradlew test
```

## Migration Progress

- [x] Basic project setup
- [x] Controller structure
- [x] Request dispatcher
- [x] Repository layer foundation
- [x] Processor implementations:
  - [x] CommonInquiryProcessor
  - [x] ApplicationMasterDataProcessor
  - [x] AuthenticationProcessor
  - [x] NotificationProcessor
  - [ ] Remaining processors
- [ ] Integration testing
- [ ] Production deployment

## Implemented Functionality

- Common inquiry operations:
  - Get FTV list
  - Get detailed FTV list
  - Get approved purchase orders
  - Get blacklisted applications
  - Convert numbers to Arabic text

- Application master data operations:
  - Get application flow
  - Get application history
  - Get application history details
  - Update application flow status
  
- Authentication operations:
  - User login
  - User logout
  - Session validation
  
- Notification operations:
  - Send notification to customer
  - Get web notifications
  - Update notification read status
  - Get message configuration
  - Get notification history


.\gradlew clean build

  copy build\libs\lsf-server-spring-1.0.0.jar release\lsf-server.jar

  cp build/libs/lsf-server-spring-1.0.0.jar release/lsf-server.jar


$body = @{
    queueName = "TO_LSF_QUEUE"
    message = '{"customerId":"0","messageType":141,"status":-1,"filledValue":0.0,"pendingId":0,"amount":0.0,"cashAccNo":"C100002560","investorAccount":"437730","tradingAccount":"437730","exchangeAccount":"null","quantity":0,"isLsf":1,"price":0.0,"commission":0.0,"vat":0.0,"contractId":-1}'
}

Invoke-RestMethod -Uri "localhost:8089/send" -Method Post -Body ($body | ConvertTo-Json) -ContentType "application/json"

  {"customerId":"0","messageType":141,"status":-1,"filledValue":0.0,"pendingId":0,"amount":0.0,"cashAccNo":"C100002560","investorAccount":"437730","tradingAccount":"437730","exchangeAccount":"null","quantity":0,"isLsf":1,"price":0.0,"commission":0.0,"vat":0.0,"contractId":-1}


  ALTER TABLE MUBASHER_LSF.L34_PURCHASE_ORDER_COMMODITIES ADD L34_BOUGHT_AMNT NUMBER(18,5) NULL;

  ALTER TABLE MUBASHER_LSF.L34_PURCHASE_ORDER_COMMODITIES MODIFY L34_BOUGHT_AMNT NUMBER(18,5);


  To DO
  1. getCustomerDetailsOrderContract - seems charges are wrong, need to check
  2. exact flow with share, commodity with physical delivery, commodity with allow to sell