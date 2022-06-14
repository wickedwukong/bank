package bank

import java.math.BigDecimal
import java.util.Currency.getInstance
import java.util.Locale.US

class Bank {
    private val accounts = mutableMapOf<Customer, Money>()

    fun totalBalance(): Money =
        accounts.toList().fold(Money(BigDecimal.ZERO, getInstance(US))) { totalBalance, (_, accountBalance) ->
            Money(totalBalance.value.plus(accountBalance.value), totalBalance.currency)
        }

    fun deposit(customer: Customer, money: Money) {
        val newBalance = accounts[customer]?.let {
            Money(it.value.plus(money.value), it.currency)
        } ?: money
        accounts[customer] = newBalance
    }

    fun balanceFor(customer: Customer): Money? {
        return this.accounts[customer]
    }

}
