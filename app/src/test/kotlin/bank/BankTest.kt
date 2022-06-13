package bank

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
}
