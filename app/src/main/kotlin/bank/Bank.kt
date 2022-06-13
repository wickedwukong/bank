package bank

import java.math.BigDecimal
import java.util.*
import java.util.Locale.US

class Bank {
    fun totalBalance(): Money {
        return Money(BigDecimal.ZERO, Currency.getInstance(US))
    }

}
