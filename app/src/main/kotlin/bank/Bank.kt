package bank

import java.math.BigDecimal
import java.util.*
import java.util.Locale.US

class Bank {
    private lateinit var balance: Money

    fun totalBalance(): Money {
        return Money(BigDecimal.ZERO, Currency.getInstance(US))
    }

    fun deposit(customer: String, money: Money) {
        this.balance = money
    }

    fun balanceFor(customer: String): Money {
        return this.balance
    }

}
