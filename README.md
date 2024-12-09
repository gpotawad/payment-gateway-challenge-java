# Instructions
This file contains the process I have followed to approach the solution
## System Requirements
- JDK 17
- Maven
- Docker

## Requirements Analysis:
- core features: payment processing (Authorized, Declined, Rejected)
- retrieval of payment details.
- key components: Shopper, Merchant, Payment Gateway, Acquiring Bank (simulated).

## Requirement Assumptions
- Considering Card payment as a single payment processor. 
- As per request and response. Authorized and Declined will be in response if bank refuses
- Above response will get saved
- Validation error is basically a Rejected status with 402
- This will not be saved in the db as its not has been passed 

## API Design
- RESTful endpoints 
  - POST /payments
  - GET /payments/{id}).
- Validation: 
  - Ensure card data (expiry, CVV, amount, etc.) adheres to requirements.
- Storage: Use an existing mock repository with List for sake of simplicity, in-memory database can be considered here.
- Simulator Integration: Integrated with the provided bank simulator to simulate real responses.

## Implementation:
- Payment Processor: Implement logic to call the bank simulator and handle responses (Authorized/Declined).
  - create a bank service same as an adapter to talk to third party API
- Payment Retrieval: Implement logic to fetch payment details using payment ID.
- Edge Case Handling: Handle invalid requests 
  - Incorrect data
  - Expired card 
  - Unsupported currency

## Design Documentation
#### Design Considerations:
The payment gateway API is designed to allow merchants to process payments and retrieve payment details. The design follows RESTful principles and provides two core functionalities:
1. **Processing Payments**: Merchants can submit payment details, including card information, amount, and currency. Based on the validation and response from the bank simulator, payments will either be authorized, declined, or rejected.
2. **Retrieving Payments**: Merchants can retrieve the payment details using a unique payment ID.

#### Assumptions:
- Payment details are stored in-memory, using a mock repository.
- The card details, including the card number (last four digits), are not masked at the moment but need to encrypt for safe transmission.
- Bank might not have security key to perform encryption / decryption.
- The simulator returns responses based on predefined test cards.
- Only ISO-4217 compliant currencies are supported.
- Max 3 currencies are implemented GBP, USD, EUR.
- Rate Limiting, Idempotency, Retry logic is not needed to complicate this simple requirement and ove engineer at this stage
- Not used authorisation_code from bank simulator, it can be used at co-relation id.

#### API Endpoints:
1. **POST /payments**: Submits payment details.
    - Request Body: `card_number`, `expiry_date`, `currency`, `amount`, `cvv`.
    - Response: Payment status (`Authorized` or `Declined`), payment ID, last four digits of card, etc.
    - Validation error returns `Bad Request` with error response.

2. **GET /payments/{id}**: Retrieves payment details by payment ID.
    - Response: Payment details, including status, card number (masked), amount, currency, etc.
    - Invalid Id returns `Not Found` with error response

#### Edge Case Handling:
- Invalid card details or missing parameters return a 400 status.
- Expired cards or invalid currencies will be rejected.
- Bank simulator disconnection and Bank can not process some cards will be rejected too.

#### Testing:
Unit tests ensure that validation, payment processing, and retrieval mechanisms work correctly. Tests also cover edge cases such as invalid inputs and expired cards.

#### Assumptions in Testing:
- The bank simulator is reliable and returns consistent responses for predefined test cases.

## Validation Rules Implementation:
#### Card Number:
Required
Between 14 and 19 characters long
Must only contain numeric characters

#### Expiry Month:
Required
Value must be between 1 and 12

#### Expiry Year:
Required
Value must be in the future (current year + month should be before the expiry month + year)

#### Currency:
Required
Must be a valid ISO currency code with 3 characters
Limit to 3 currency codes [GBP, USD, EUR]

#### Amount:
Required
Integer value representing minor currency units (e.g., USD 0.01 = 1, USD 10.50 = 1050)

#### CVV:
Required
Must be 3 or 4 digits long
Must only contain numeric characters



