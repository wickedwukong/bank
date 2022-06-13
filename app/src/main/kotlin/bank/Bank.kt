package bank

import java.math.BigDecimal
import java.util.*
import java.util.Locale.US

class Bank {
    private val accounts = mutableMapOf<String, Money>()

    fun totalBalance(): Money {
        return Money(BigDecimal.ZERO, Currency.getInstance(US))
    }

    fun deposit(customer: String, money: Money) {
        val newBalance = accounts[customer]?.let {
            Money(it.value.plus(money.value), it.currency)
        }?:money
        accounts[customer] = newBalance
    }

    fun balanceFor(customer: String): Money? {
        return this.accounts[customer]
    }

}
