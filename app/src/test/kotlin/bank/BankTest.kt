package bank

import java.math.BigDecimal.*
import java.util.Currency.getInstance
import java.util.Locale.UK
import java.util.Locale.US
import kotlin.test.Test
import kotlin.test.assertEquals

class BankTest {
    @Test
    fun `Bank without customer account should have zero balance`() {
        assertEquals(Money(ZERO, getInstance(US)), Bank(getInstance(US)).totalBalance())
        assertEquals(Money(ZERO, getInstance(UK)), Bank(getInstance(UK)).totalBalance())
    }

    @Test
    fun `A customer's first deposit is the customer's balance`() {
        val bank = Bank(getInstance(US))
        bank.deposit(Customer("Alice"), ONE)
        assertEquals(Money(ONE, getInstance(US)), bank.balanceFor(Customer("Alice")))
    }
    @Test
    fun `Deposit in another currency (UK GBP)`() {
        val bank = Bank(getInstance(UK))
        bank.deposit(Customer("Alice"), ONE)
        assertEquals(Money(ONE, getInstance(UK)), bank.balanceFor(Customer("Alice")))
    }

    @Test
    fun `should maintain balance for multiple customers and multiple deposits`() {
        val bank = Bank(getInstance(US))
        bank.deposit(Customer("Alice"), ONE)
        bank.deposit(Customer("Alice"), TEN)
        bank.deposit(Customer("Bob"), TEN)
        assertEquals(Money(valueOf(11), getInstance(US)), bank.balanceFor(Customer("Alice")))
        assertEquals(Money(TEN, getInstance(US)), bank.balanceFor(Customer("Bob")))
    }


    @Test
    fun `A customer's multiple deposits should be the accumulated balance`() {
        val bank = Bank(getInstance(US))
        bank.deposit(Customer("Alice"), ONE)
        bank.deposit(Customer("Alice"), TEN)
        assertEquals(Money(valueOf(11), getInstance(US)), bank.balanceFor(Customer("Alice")))
    }

    @Test
    fun `Bank's total balance should be the accumulated customer deposits - single customer`() {
        val bank = Bank(getInstance(US))
        bank.deposit(Customer("Alice"), ONE)
        bank.deposit(Customer("Alice"), TEN)
        assertEquals(Money(valueOf(11), getInstance(US)), bank.totalBalance())
    }
}
