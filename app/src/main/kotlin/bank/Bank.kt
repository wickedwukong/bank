package bank

import dev.forkhandles.result4k.*
import java.math.BigDecimal
import java.util.*

class Bank(val currency: Currency) {
    private val accounts = mutableMapOf<Customer, Money>()

    fun totalBalance(): Money =
        accounts.toList().fold(Money(BigDecimal.ZERO, currency)) { totalBalance, (_, accountBalance) ->
            Money(totalBalance.value.plus(accountBalance.value), totalBalance.currency)
        }

    fun deposit(customer: Customer, deposit: Money): Result4k<Money, BankError> =
        validateCurrency(deposit)
            .map { validDeposit ->
                accounts[customer]?.let {
                    Money(it.value.plus(validDeposit.value), currency)
                } ?: validDeposit
            }.peek { newBalance ->
                accounts[customer] = newBalance
            }.map { deposit }

    private fun validateCurrency(moneyAmount: Money): Result4k<Money, BankError> =
        if (moneyAmount.currency == currency) {
            Success(moneyAmount)
        } else {
            Failure(UnSupportedCurrencyError(currency, moneyAmount.currency))
        }

    fun balanceFor(customer: Customer): Money? {
        return this.accounts[customer]
    }

    fun withdraw(customer: Customer, withdrawingAmount: Money): Result4k<Money, BankError> {
        if (withdrawingAmount.value <= BigDecimal.ZERO) {
            return Failure(AmountHasToBeMoreThanZeroError(withdrawingAmount))
        }

        return accounts[customer]?.let {
            if (it.value > withdrawingAmount.value) {
                accounts[customer] = Money(it.value.minus(withdrawingAmount.value), currency)
                Success(withdrawingAmount)
            } else {
                Failure(WithdrawExceedingBalanceError(customer, Money(BigDecimal.ONE, Currency.getInstance(Locale.US))))
            }
        } ?: Failure(UnknownCustomerError(customer))
    }

}
