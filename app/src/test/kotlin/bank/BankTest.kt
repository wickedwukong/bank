package bank

import java.math.BigDecimal.ONE
import java.math.BigDecimal.ZERO
import java.util.Currency.getInstance
import java.util.Locale.US
import kotlin.test.Test
import kotlin.test.assertEquals

class BankTest {
    @Test
    fun `Bank without customer account should have zero balance`() {
        assertEquals(Money(ZERO, getInstance(US)), Bank().totalBalance())
    }

    @Test
    fun `A customer's first deposit is the customer's balance`() {
        val bank = Bank()
        bank.deposit("Alice", Money(ONE, getInstance(US)))
        assertEquals(Money(ONE, getInstance(US)), bank.balanceFor("Alice"))
    }
}
