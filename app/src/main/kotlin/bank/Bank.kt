package bank

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import java.math.BigDecimal
import java.util.*

class Bank(val currency: Currency) {
    private val accounts = mutableMapOf<Customer, Money>()

    fun totalBalance(): Money =
        accounts.toList().fold(Money(BigDecimal.ZERO, currency)) { totalBalance, (_, accountBalance) ->
            Money(totalBalance.value.plus(accountBalance.value), totalBalance.currency)
        }

    fun deposit(customer: Customer, moneyAmount: Money) {
        val newBalance = accounts[customer]?.let {
            Money(it.value.plus(moneyAmount.value), currency)
        } ?: moneyAmount

        accounts[customer] = newBalance
    }

    fun balanceFor(customer: Customer): Money? {
        return this.accounts[customer]
    }

    fun withdraw(customer: Customer, moneyAmount: BigDecimal): Result4k<Money, BankError> {
        if (moneyAmount <= BigDecimal.ZERO) {
            return Failure(AmountHasToBeMoreThanZeroError(Money(moneyAmount, currency)))
        }

        return accounts[customer]?.let {
            if (it.value > moneyAmount) {
                accounts[customer] = Money(it.value.minus(moneyAmount), currency)
                Success(Money(moneyAmount, currency))
            } else {
                Failure(WithdrawExceedingBalanceError(customer, Money(BigDecimal.ONE, Currency.getInstance(Locale.US))))
            }
        } ?: Failure(UnknownCustomerError(customer))
    }

}
