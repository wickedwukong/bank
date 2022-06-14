package bank

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.map
import java.math.BigDecimal
import java.util.*

class Bank(val currency: Currency) {
    private val accounts = mutableMapOf<Customer, Money>()

    fun totalBalance(): Money =
        accounts.toList().fold(Money(BigDecimal.ZERO, currency)) { totalBalance, (_, accountBalance) ->
            Money(totalBalance.value.plus(accountBalance.value), totalBalance.currency)
        }

    fun deposit(customer: Customer, moneyAmount: BigDecimal) {
        val newBalance = accounts[customer]?.let {
            Money(it.value.plus(moneyAmount), currency)
        } ?: Money(moneyAmount, currency)

        accounts[customer] = newBalance
    }

    fun balanceFor(customer: Customer): Money? {
        return this.accounts[customer]
    }

    fun withdraw(customer: Customer, moneyAmount: BigDecimal): Result4k<Money, BankError> {
        return accounts[customer]?.let {
            Money(it.value.minus(moneyAmount), currency)
        }?.let {
            accounts[customer] = it
            Success(it)
        } ?: Failure(UnknownCustomerError(customer))
    }

}
