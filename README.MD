1. The solution is implemented in Kotlin
2. How to build and run tests

   `./gradlew clean test`
   `./gradlew clean build`
3. Analysis, assumption & limitations
   
   * The Bank lets different customers to deposit, withdraw, check balance
   * The Bank maintains each customer's balance after a deposit and withdraw therefore it needs a way to track each customer's account
   * Total balance can be dynamically calculated by accumulating all customer's balance
   * The Bank needs to handle invalid input, e.g. negative deposit, zero withdraw & unsupported currency etc. For example, the result of a
     deposit can be either a `Success`, the deposited amount, or a `Failure` which should inform us what went wrong.
   * Single currency or multiple currency support? Supporting multiple currency will require either currency conversion
     or maintaining multiple currency account for a single customer. This will dramatically complicate the solution. The solution
     supports single currency only, not multiple currencies.
   * Fixed USD currency support or currency is configurable? The solution could be made a lot simpler if we only need to support a fixed USD currency.
     In that case, we could have a `USDMoney` class which has a fixed USD currency and a numeric value. I have decided to support configurable currency
     at the `Bank` level. Therefor the `Money` needs to have both value and currency configurable and the `Bank` need to deals with unsupported
     currency scenarios. In the real life project, a conversation will be had to clarify the requirements.
   * Concurrency is not addressed to simplify design. The Bank is not thread safe.

4. Design
   * The main class is `Bank`. 
   * `Bank` provides all entry points for interacting with the system. e.g. deposit & checking balance etc
   * `Bank` supports a single currency which is set when a new `Bank` is instantiated. The currency can not be changed after
   * Deposit, withdraw and balance amount etc. are represented as `Money`. Money is more than a numeric value. It has a numeric value
     and a currency. `Money` could also be the place with Money related behaviors if our system gets complicated.
   * The `Money` in this solution is anemic. It does not have any behavior. It could be made to have more behaviors, e.g. money amount sum & subtraction,
     if our system gets more complicated.
   * All the money amount calculation, e.g. sum & subtraction are implemented in `Bank`. This is because our system is
     simple enough to have all these logics in `Bank`.  
   * Customers are represented as `Customer` instead of a primitive type, e.g a String. This is because Customer is
     often one of the most important domain concept with behavior and identification in real systems, although 
     a primitive type could be work for our requirements.
   * The `Bank` deals with invalid input. It uses [`Result(4K)`](https://github.com/npryce/result4k) to represent an outcome which can be either a `Success` or a `Failure`.
     The reason `Result` is used is because 
     1. "Kotlin does not type-check exceptions. Result4k lets you type-check code that reports and recovers from errors."
     2. The alternative is to use exception. This does not inform the client that error scenarios need to be dealt with
     3. "Result4k works with the grain of the Kotlin language." By using `map`, `flatMap` & `peek` etc. code can be written
        in a fluent, pipeline fashion when we need to perform one action after the other and their results need to be aggregated , much like 
        working with multiple Kotlin collections or null to transform a result. 
     4. `Result4K` has a tiny footprint and it has been battle tested. I'm very familiar with it. 
   * `map` is used to track Customer's account to allow fast lookup
5. Error representation 
   * `BankError` is a sealed interface so that the system knows all possible errors
   * `BankError` has a set of errors representing a few error scenarios. e.g. negative deposit & unsupported currency
   * Each `BankError` captures some information to provide an informative message to show what went wrong
6. Dependency
   The system's only external dependency is [`Result(4K)`](https://github.com/npryce/result4k). [See here](https://github.com/npryce/result4k) See above for reasons why it is used
