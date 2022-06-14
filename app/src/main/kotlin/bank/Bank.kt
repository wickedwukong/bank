package bank

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

}
