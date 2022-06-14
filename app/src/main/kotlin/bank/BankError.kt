package bank

sealed interface BankError {
    val message: String
}

data class WithdrawExceedingBalanceError(val customer: Customer, val attemptedWithdrawAmount: Money) : BankError {
    override val message: String
        get() = "Customer ${customer.id} attempted to withdraw more than available balance. Attempted amount: $attemptedWithdrawAmount"

}
data class UnknownCustomerError(val customer: Customer) : BankError {
    override val message: String
        get() = "Customer ${customer.id} is unknown"
}
