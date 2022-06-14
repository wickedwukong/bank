package bank

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.failureOrNull
import dev.forkhandles.result4k.get
import java.math.BigDecimal.*
import java.util.Currency.getInstance
import java.util.Locale.UK
import java.util.Locale.US
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class BankTest {
    @Test
    fun `should have zero total balance for a newly established Bank in the right currency`() {
        assertEquals(Money(ZERO, getInstance(US)), Bank(getInstance(US)).totalBalance())
        assertEquals(Money(ZERO, getInstance(UK)), Bank(getInstance(UK)).totalBalance())
    }

    @Test
    fun `should maintain the balance for a single customer's single deposit`() {
        val bank = Bank(getInstance(US))
        assertEquals(
            Success(Money(ONE, getInstance(US))),
            bank.deposit(Customer("Alice"), Money(ONE, getInstance(US)))
        )

        assertEquals(Money(ONE, getInstance(US)), bank.balanceFor(Customer("Alice")))
    }

    @Test
    fun `should result in error when depositing unsupported currency`() {
        val bank = Bank(getInstance(US))
        assertEquals(
            Failure(UnSupportedCurrencyError(getInstance(US), getInstance(UK))),
            bank.deposit(Customer("Alice"), Money(ONE, getInstance(UK)))
        )

        assertNull(bank.balanceFor(Customer("Alice")))
    }

    @Test
    fun `should have no balance for an unknown customer`() {
        val bank = Bank(getInstance(US))

        assertNull(bank.balanceFor(Customer("Alice")))
    }

    @Test
    fun `should maintain the balance for a single customer's single deposit in a different currency (GBP)`() {
        val bank = Bank(getInstance(UK))
        bank.deposit(Customer("Alice"), Money(ONE, getInstance(UK)))

        assertEquals(Money(ONE, getInstance(UK)), bank.balanceFor(Customer("Alice")))
    }

    @Test
    fun `should maintain the accumulated balance for a single customer's across multiple deposits`() {
        val bank = Bank(getInstance(US))
        bank.deposit(Customer("Alice"), Money(ONE, getInstance(US)))
        bank.deposit(Customer("Alice"), Money(TEN, getInstance(US)))

        assertEquals(Money(valueOf(11), getInstance(US)), bank.balanceFor(Customer("Alice")))
    }


    @Test
    fun `should maintain accumulated balance for multiple customers across multiple deposits`() {
        val bank = Bank(getInstance(US))
        bank.deposit(Customer("Alice"), Money(ONE, getInstance(US)))
        bank.deposit(Customer("Alice"), Money(TEN, getInstance(US)))
        bank.deposit(Customer("Bob"), Money(TEN, getInstance(US)))

        assertEquals(Money(valueOf(11), getInstance(US)), bank.balanceFor(Customer("Alice")))
        assertEquals(Money(TEN, getInstance(US)), bank.balanceFor(Customer("Bob")))
    }

    @Test
    fun `should maintain the total accumulated balance for the Bank's across customer deposits - single customer`() {
        val bank = Bank(getInstance(US))
        bank.deposit(Customer("Alice"), Money(ONE, getInstance(US)))
        bank.deposit(Customer("Alice"), Money(TEN, getInstance(US)))

        assertEquals(Money(valueOf(11), getInstance(US)), bank.totalBalance())
    }

    @Test
    fun `should maintain the total accumulated balance for the Bank's across customer deposits - multiple customers`() {
        val bank = Bank(getInstance(US))
        bank.deposit(Customer("Alice"), Money(ONE, getInstance(US)))
        bank.deposit(Customer("Alice"), Money(TEN, getInstance(US)))
        bank.deposit(Customer("Bob"), Money(TEN, getInstance(US)))

        assertEquals(Money(valueOf(21), getInstance(US)), bank.totalBalance())
    }

    @Test
    fun `should result in error when withdrawing for an unknown customer and Bank's total balance is not changed`() {
        val bank = Bank(getInstance(US))
        assertEquals(
            UnknownCustomerError(Customer("UnknownCustomerId")),
            bank.withdraw(Customer("UnknownCustomerId"), Money(ONE, bank.currency)).failureOrNull()
        )

        assertEquals(Money(ZERO, getInstance(US)), bank.totalBalance())
    }

    @Test
    fun `should let customer withdraw money which amount is less than existing balance and maintain customer's new balance after withdraw`() {
        val bank = Bank(getInstance(US))
        bank.deposit(Customer("Alice"), Money(TEN, getInstance(US)))

        assertEquals(Money(ONE, getInstance(US)), bank.withdraw(Customer("Alice"), Money(ONE, bank.currency)).get())
        assertEquals(Money(valueOf(9), getInstance(US)), bank.balanceFor(Customer("Alice")))
    }
    @Test
    fun `should let customer withdraw all of the customer's balance and the customer's balance should be zero after the withdraw`() {
        val bank = Bank(getInstance(US))
        bank.deposit(Customer("Alice"), Money(TEN, getInstance(US)))

        assertEquals(Money(TEN, getInstance(US)), bank.withdraw(Customer("Alice"), Money(TEN, bank.currency)).get())
        assertEquals(Money(ZERO, getInstance(US)), bank.balanceFor(Customer("Alice")))
    }

    @Test
    fun `should result in error when customer withdraws more than existing balance and customer's balance remains unchanged`() {
        val bank = Bank(getInstance(US))
        bank.deposit(Customer("Alice"), Money(ONE, getInstance(US)))

        assertEquals(
            WithdrawExceedingBalanceError(Customer("Alice"), Money(valueOf(2), getInstance(US))),
            bank.withdraw(Customer("Alice"), Money(valueOf(2), bank.currency)).failureOrNull()
        )

        assertEquals(Money(ONE, getInstance(US)), bank.balanceFor(Customer("Alice")))
    }

    @Test
    fun `should result in error when withdrawing Zero or less than Zero amount`() {
        val bank = Bank(getInstance(US))
        bank.deposit(Customer("Alice"), Money(ONE, getInstance(US)))

        assertEquals(
            AmountHasToBePositiveError(Money(valueOf(0), getInstance(US))),
            bank.withdraw(Customer("Alice"), Money(ZERO, bank.currency)).failureOrNull()
        )
        assertEquals(Money(ONE, getInstance(US)), bank.balanceFor(Customer("Alice")))

        assertEquals(
            AmountHasToBePositiveError(Money(valueOf(-1), getInstance(US))),
            bank.withdraw(Customer("Alice"), Money(valueOf(-1), bank.currency)).failureOrNull()
        )

        assertEquals(Money(ONE, getInstance(US)), bank.balanceFor(Customer("Alice")))
    }

    @Test
    fun `should result in error when withdrawing unsupported currency and maintain customer's original balance and bank's total balance`() {
        val bank = Bank(getInstance(US))
        bank.deposit(Customer("Alice"), Money(ONE, getInstance(US)))

        assertEquals(
            Failure(UnSupportedCurrencyError(getInstance(US), getInstance(UK))),
            bank.withdraw(Customer("Alice"), Money(ONE, getInstance(UK)))
        )

        assertEquals(Money(ONE, getInstance(US)), bank.balanceFor(Customer("Alice")))
        assertEquals(Money(ONE, getInstance(US)), bank.totalBalance())
    }

    @Test
    fun `should maintain bank's total balance with various deposits and withdraws across multiple customers`() {
        val bank = Bank(getInstance(US))
        bank.deposit(Customer("Alice"), Money(ONE, getInstance(US)))
        assertEquals(Money(ONE, getInstance(US)), bank.totalBalance())

        bank.deposit(Customer("Bob"), Money(ONE, getInstance(US)))
        assertEquals(Money(valueOf(2), getInstance(US)), bank.totalBalance())

        bank.deposit(Customer("Alice"), Money(ONE, getInstance(US)))
        assertEquals(Money(valueOf(3), getInstance(US)), bank.totalBalance())

        bank.withdraw(Customer("Alice"), Money(ONE, getInstance(US)))
        assertEquals(Money(valueOf(2), getInstance(US)), bank.totalBalance())

        bank.withdraw(Customer("Alice"), Money(ONE, getInstance(US)))
        assertEquals(Money(ZERO, getInstance(US)), bank.balanceFor(Customer("Alice")))
        assertEquals(Money(ONE, getInstance(US)), bank.balanceFor(Customer("Bob")))
        assertEquals(Money(valueOf(1), getInstance(US)), bank.totalBalance())
    }
}
