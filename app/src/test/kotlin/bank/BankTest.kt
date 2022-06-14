package bank

import java.math.BigDecimal.*
import java.util.Currency.getInstance
import java.util.Locale.UK
import java.util.Locale.US
import kotlin.test.Test
import kotlin.test.assertEquals

class BankTest {
    @Test
    fun `should have zero total balance for a newly established Bank in the right currency`() {
        assertEquals(Money(ZERO, getInstance(US)), Bank(getInstance(US)).totalBalance())
        assertEquals(Money(ZERO, getInstance(UK)), Bank(getInstance(UK)).totalBalance())
    }

    @Test
    fun `should maintain the balance for a single customer's single deposit`() {
        val bank = Bank(getInstance(US))
        bank.deposit(Customer("Alice"), ONE)
        assertEquals(Money(ONE, getInstance(US)), bank.balanceFor(Customer("Alice")))
    }
    @Test
    fun `should maintain the balance for a single customer's single deposit in anthoer currency (GBP)`() {
        val bank = Bank(getInstance(UK))
        bank.deposit(Customer("Alice"), ONE)
        assertEquals(Money(ONE, getInstance(UK)), bank.balanceFor(Customer("Alice")))
    }

    @Test
    fun `should maintain the accumulated balance for a single customer's across multiple deposits`() {
        val bank = Bank(getInstance(US))
        bank.deposit(Customer("Alice"), ONE)
        bank.deposit(Customer("Alice"), TEN)
        assertEquals(Money(valueOf(11), getInstance(US)), bank.balanceFor(Customer("Alice")))
    }


    @Test
    fun `should maintain accumulated balance for multiple customers across multiple deposits`() {
        val bank = Bank(getInstance(US))
        bank.deposit(Customer("Alice"), ONE)
        bank.deposit(Customer("Alice"), TEN)
        bank.deposit(Customer("Bob"), TEN)
        assertEquals(Money(valueOf(11), getInstance(US)), bank.balanceFor(Customer("Alice")))
        assertEquals(Money(TEN, getInstance(US)), bank.balanceFor(Customer("Bob")))
    }

    @Test
    fun `should maintain the total accumulated balance for the Bank's across customer deposits - single customer`() {
        val bank = Bank(getInstance(US))
        bank.deposit(Customer("Alice"), ONE)
        bank.deposit(Customer("Alice"), TEN)
        assertEquals(Money(valueOf(11), getInstance(US)), bank.totalBalance())
    }

    @Test
    fun `should maintain the total accumulated balance for the Bank's across customer deposits - multiple customers`() {
        val bank = Bank(getInstance(US))
        bank.deposit(Customer("Alice"), ONE)
        bank.deposit(Customer("Alice"), TEN)
        bank.deposit(Customer("Bob"), TEN)
        assertEquals(Money(valueOf(21), getInstance(US)), bank.totalBalance())
    }
}
