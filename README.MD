1. How to build and run tests

   `./gradlew clean test`
   `./gradlew clean build`
2. Analysis, assumption & limitations
   
   * The Bank lets different customers to deposit, withdraw, check balance
   * The Bank maintains each customer's balance after a deposit and withdraw therefore it needs a way to track each customer's account
   * Total balance can be dynamically calculated by accumulating all customer's balance
   * The Bank needs to handle invalid input, e.g. negative deposit, zero withdraw & unsupported currency etc. For example, the result of a
     deposit can be either a success, the deposited amount, or a failure which should inform us what went wrong.
   * Single currency or multiple currency support? Supporting multiple currency will require either currency conversion
     or maintaining multiple currency account for a single customer. This will dramatically complicate the design. The solution
     will support single currency not multiple currency.
   * Concurrency is not addressed to simplify design. The Bank is not thread safe.

3. Design
   * The main class is `Bank`. 
   * `Bank` provides all entry points for interacting with the system. e.g. deposit & checking balance etc
   * `Bank` supports a single currency which is set when a new `Bank` is instantiated. The currency can not be changed after
   * Deposit, withdraw and balance amount etc. are represented as `Money`, as Money is more than a number. It has a numeric value
     and a currency.
   * The `Money` is anemic. It does not have any behavior. It could be made to have more behaviors, e.g. money amount sum & subtraction,
     in a slightly more complicated system
   * All the money amount calculation, e.g. sum & subtraction are implemented in `Bank`. This is because our system is
     simple enough to have all these logics in `Bank`. These logics can be moved to `Money` if the system gets more complicated  
   * Customers are represented as `Customer` instead of a primitive type, e.g a String. This is because Customer is
     often one of the most important domain concept with behavior and identification in real systems, although 
     a primitive type could be enough for our requirements.
   * The `Bank` deals with invalid input. It uses `Result(4K)` to represent an outcome which can be either a success or a failure.
     The reason `Result` is used is because 
     1. "Kotlin does not type-check exceptions. Result4k lets you type-check code that reports and recovers from errors."
     2. The alternative is to use exception. This does not inform the client that error scenarios need to be dealt with
     3. "Result4k works with the grain of the Kotlin language." By using `map`, `flatMap` & `peek` etc. code can be written
        in a fluent fashion, much like working with Kotlin collection or null to transform a result
     4. `Result4K` has a tiny footprint and it has been battle tested 
4. main class
5. https://github.com/npryce/result4k
6. edge cases/unhappy path: customer does not exist. withdraw negative values. deposit zero & negative values
7. Why choosing map
8. Why data class customer?
9. Assumption: only support a single currency?
10. why sum and subtraction logics are in Bank not Money

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
6. Validation on deposit and withdraw

Supported & tested scenarios:

