package bank

interface BankError {
    val message: String
}

data class UnknownCustomerError(val customer: Customer) : BankError {
    override val message: String
        get() = "Customer ${customer.id} is unknown"
}
