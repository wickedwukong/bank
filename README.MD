1. How to build and run tests (gradle)
2. analysis
3. main class
4. https://github.com/npryce/result4k
5. edge cases/unhappy path: customer does not exist. withdraw negative values. deposit zero & negative values
6. Why choosing map
7. Why data class customer?
8. Assumption: only support a single currency?
9. why sum and subtraction logics are in Bank not Money

TODO:
implement and test deposit zero or negative values

The main class is `Bank`. The `Bank` supports the following features:

Assumption:
The Bank only supports a single currency. Currency is configured when `Bank` is initiated. Having the `Bank` deal with
a different currency results in a `Failure` with a `UnSupportedCurrencyError` (See detailed scenarios below.) when 
depositing or withdrawing

Thread safety is not a concern. Concurrent access to a customer account modifying balance is not addressed in the solution.

Overall design:

The main class is `Bank`. The `Bank` supports the following features:

1. Deposit
2. Withdraw
3. Checking balance for a customer
4. Total Bank balance 
5. Multiple customers

Supported & tested scenarios:

# Deposit:

1. Deposit a regular amount for an existing customer results in a `Success` with the deposit amount returned 
2. Deposit a regular amount for a non-existing customer
3. Deposit a negative or Zero amount results in a `Failure` with a specific error
4. Deposit an amount with unsupported currency results in a `Failure` with a `UnSupportedCurrencyError`

# Withdraw

1. Withdraw a regular amount, i.e. less than existing amount, results in a `Success` with the withdrew amount returned
2. Withdraw all of a customer's balance results in a `Success` with the withdrew amount returned. Customer's balance becomes Zero
3. Withdraw a negative or Zero amount results in a `Failure` with a specific error
4. Withdraw an amount with unsupported currency results in `Failure` with a `UnSupportedCurrencyError`
5. Withdraw more than existing balance results in `Failure` with a specific error

# Checking balance for a customer

1. Checking balance for a customer after a deposit
2. Checking balance for a customer after multiple deposits
3. Checking balance for a customer after a withdraw
4. Checking balance for a customer after a withdraw of all of a customer's balance
5. Checking balance for a customer after multiple withdraws
6. Checking balance after a deposit and withdraw error
7. Checking balance after a non-existing customer.



# Total Bank balance

1. A bank without any customer (i.e a new bank) should have Zero balance
2. Total bank balance after a single customer deposit
3. Total bank balance after a single customer's multiple deposit
4. Total bank balance after multi-customer's multiple deposits
5. Total bank balance after a single withdraw
6. Total bank balance after multiple withdraws across multiple customers
7. Total bank balance after a failed withdraw
8. Total bank balance after a failed deposit

# Multiple customers

1. Multiple customer's balance are maintained after a deposits and withdraws



