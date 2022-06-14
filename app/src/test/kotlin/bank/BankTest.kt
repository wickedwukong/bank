package bank

import java.math.BigDecimal.*
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

    @Test
    fun `A customer's multiple deposits should be the accumulated balance`() {
        val bank = Bank()
        bank.deposit("Alice", Money(ONE, getInstance(US)))
        bank.deposit("Alice", Money(TEN, getInstance(US)))
        assertEquals(Money(valueOf(11), getInstance(US)), bank.balanceFor("Alice"))
    }

    @Test
    fun `Bank's total balance should be the accumulated customer deposits - single customer`() {
        val bank = Bank()
        bank.deposit("Alice", Money(ONE, getInstance(US)))
        bank.deposit("Alice", Money(TEN, getInstance(US)))
        assertEquals(Money(valueOf(11), getInstance(US)), bank.totalBalance())
    }
}
