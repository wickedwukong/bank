package bank

import dev.forkhandles.result4k.*
import java.math.BigDecimal
import java.math.BigDecimal.*
import java.util.*
import java.util.Currency.getInstance
import java.util.Locale.UK
import java.util.Locale.US
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BankTest {
    @Test
    fun `should have zero total balance for a newly established Bank in the right currency`() {
        assertEquals(usdOf(ZERO), Bank(currencyOf(US)).totalBalance())
        assertEquals(gbpOf(ZERO), Bank(currencyOf(UK)).totalBalance())
    }

    @Test
    fun `should maintain the balance for a single customer's single deposit`() {
        val bank = Bank(currencyOf(US))
        
        assertEquals(usdOf(ONE), bank.deposit(Customer("Alice"), usdOf(ONE)).valueOrNull())
        assertEquals(usdOf(ONE), bank.balanceFor(Customer("Alice")))
    }

    @Test
    fun `should result in error when depositing unsupported currency`() {
        val bank = Bank(currencyOf(US))
        
        assertEquals(
            Failure(UnSupportedCurrencyError(currencyOf(US), currencyOf(UK))),
            bank.deposit(Customer("Alice"), gbpOf(ONE))
        )
        assertNull(bank.balanceFor(Customer("Alice")))
    }

    @Test
    fun `should have no balance for an unknown customer`() {
        val bank = Bank(currencyOf(US))

        assertNull(bank.balanceFor(Customer("Alice")))
    }

    @Test
    fun `should maintain the balance for a single customer's single deposit in a different currency (GBP)`() {
        val bank = Bank(currencyOf(UK))
        bank.deposit(Customer("Alice"), gbpOf(ONE))

        assertEquals(gbpOf(ONE), bank.balanceFor(Customer("Alice")))
    }

    @Test
    fun `should maintain the accumulated balance for a single customer's across multiple deposits`() {
        val bank = Bank(currencyOf(US))
        bank.deposit(Customer("Alice"), usdOf(ONE))
        bank.deposit(Customer("Alice"), usdOf(TEN))

        assertEquals(usdOf(valueOf(11)), bank.balanceFor(Customer("Alice")))
    }


    @Test
    fun `should maintain accumulated balance for multiple customers across multiple deposits`() {
        val bank = Bank(currencyOf(US))
        bank.deposit(Customer("Alice"), usdOf(ONE))
        bank.deposit(Customer("Alice"), usdOf(TEN))
        bank.deposit(Customer("Bob"), usdOf(TEN))

        assertEquals(usdOf(valueOf(11)), bank.balanceFor(Customer("Alice")))
        assertEquals(usdOf(TEN), bank.balanceFor(Customer("Bob")))
    }

    @Test
    fun `should maintain the total accumulated balance for the Bank's across customer deposits - single customer`() {
        val bank = Bank(currencyOf(US))
        bank.deposit(Customer("Alice"), usdOf(ONE))
        bank.deposit(Customer("Alice"), usdOf(TEN))

        assertEquals(usdOf(valueOf(11)), bank.totalBalance())
    }

    @Test
    fun `should maintain the total accumulated balance for the Bank's across customer deposits - multiple customers`() {
        val bank = Bank(currencyOf(US))
        bank.deposit(Customer("Alice"), usdOf(ONE))
        bank.deposit(Customer("Alice"), usdOf(TEN))
        bank.deposit(Customer("Bob"), usdOf(TEN))

        assertEquals(Money(valueOf(21), currencyOf(US)), bank.totalBalance())
    }

    @Test
    fun `should result in error when attempting depositing zero or negative amount, customer and Bank's total balance should not be changed`() {
        val bank = Bank(currencyOf(US))
        bank.deposit(Customer("Alice"), Money(ONE, bank.currency))


        assertEquals(
            AmountHasToBePositiveError(Money(valueOf(0), currencyOf(US))),
            bank.deposit(Customer("Alice"), usdOf(ZERO)).failureOrNull()
        )

        assertEquals(
            AmountHasToBePositiveError(Money(valueOf(-1), currencyOf(US))),
            bank.deposit(Customer("Alice"), usdOf(valueOf(-1))).failureOrNull()
        )

        assertEquals(usdOf(ONE), bank.balanceFor(Customer("Alice")))
        assertEquals(usdOf(ONE), bank.totalBalance())
    }


    @Test
    fun `should result in error when withdrawing for an unknown customer and Bank's total balance is not changed`() {
        val bank = Bank(currencyOf(US))
        assertEquals(
            UnknownCustomerError(Customer("UnknownCustomerId")),
            bank.withdraw(Customer("UnknownCustomerId"), usdOf(ONE)).failureOrNull()
        )

        assertEquals(usdOf(ZERO), bank.totalBalance())
    }

    @Test
    fun `should let customer withdraw money which amount is less than existing balance and maintain customer's new balance after withdraw`() {
        val bank = Bank(currencyOf(US))
        bank.deposit(Customer("Alice"), usdOf(TEN))

        assertEquals(usdOf(ONE), bank.withdraw(Customer("Alice"), usdOf(ONE)).get())
        assertEquals(usdOf(valueOf(9)), bank.balanceFor(Customer("Alice")))
    }

    @Test
    fun `should let customer withdraw all of the customer's balance and the customer's balance should be zero after the withdraw`() {
        val bank = Bank(currencyOf(US))
        bank.deposit(Customer("Alice"), usdOf(TEN))

        assertEquals(usdOf(TEN), bank.withdraw(Customer("Alice"), usdOf(TEN)).get())
        assertEquals(usdOf(ZERO), bank.balanceFor(Customer("Alice")))
    }

    @Test
    fun `should result in error when customer withdraws more than existing balance and customer's balance remains unchanged`() {
        val bank = Bank(currencyOf(US))
        bank.deposit(Customer("Alice"), usdOf(ONE))

        assertEquals(
            WithdrawExceedingBalanceError(Customer("Alice"), usdOf(valueOf(2))),
            bank.withdraw(Customer("Alice"), usdOf(valueOf(2))).failureOrNull()
        )
        assertEquals(usdOf(ONE), bank.balanceFor(Customer("Alice")))
    }

    @Test
    fun `should result in error when withdrawing Zero or less than Zero amount Customer's balance and Bank's total balance are unchanged`() {
        val bank = Bank(currencyOf(US))
        bank.deposit(Customer("Alice"), usdOf(ONE))

        assertEquals(
            AmountHasToBePositiveError(Money(valueOf(0), currencyOf(US))),
            bank.withdraw(Customer("Alice"), usdOf(ZERO)).failureOrNull()
        )
        assertEquals(usdOf(ONE), bank.balanceFor(Customer("Alice")))

        assertEquals(
            AmountHasToBePositiveError(Money(valueOf(-1), currencyOf(US))),
            bank.withdraw(Customer("Alice"), usdOf(valueOf(-1))).failureOrNull()
        )

        assertEquals(usdOf(ONE), bank.balanceFor(Customer("Alice")))
        assertEquals(usdOf(ONE), bank.totalBalance())
    }

    @Test
    fun `should result in error when withdrawing unsupported currency and maintain customer's original balance and bank's total balance`() {
        val bank = Bank(currencyOf(US))
        bank.deposit(Customer("Alice"), usdOf(ONE))

        assertEquals(
            Failure(UnSupportedCurrencyError(currencyOf(US), currencyOf(UK))),
            bank.withdraw(Customer("Alice"), gbpOf(ONE))
        )

        assertEquals(usdOf(ONE), bank.balanceFor(Customer("Alice")))
        assertEquals(usdOf(ONE), bank.totalBalance())
    }

    @Test
    fun `should maintain bank's total balance with various deposits and withdraws across multiple customers`() {
        val bank = Bank(currencyOf(US))
        bank.deposit(Customer("Alice"), usdOf(ONE))
        assertEquals(usdOf(ONE), bank.totalBalance())

        bank.deposit(Customer("Bob"), usdOf(ONE))
        assertEquals(usdOf(valueOf(2)), bank.totalBalance())

        bank.deposit(Customer("Alice"), usdOf(ONE))
        assertEquals(usdOf(valueOf(3)), bank.totalBalance())

        bank.withdraw(Customer("Alice"), usdOf(ONE))
        assertEquals(usdOf(valueOf(2)), bank.totalBalance())

        bank.withdraw(Customer("Alice"), usdOf(ONE))
        assertEquals(usdOf(ZERO), bank.balanceFor(Customer("Alice")))
        assertEquals(usdOf(ONE), bank.balanceFor(Customer("Bob")))
        assertEquals(usdOf(ONE), bank.totalBalance())
    }

    private fun usdOf(value: BigDecimal) = Money(value, currencyOf(US))
    private fun gbpOf(value: BigDecimal) = Money(value, currencyOf(UK))
    private fun currencyOf(locale: Locale) = getInstance(locale)
}
