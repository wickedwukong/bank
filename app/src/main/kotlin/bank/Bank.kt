package bank

import dev.forkhandles.result4k.*
import java.math.BigDecimal
import java.util.*

class Bank(val currency: Currency) {
    private val accounts = mutableMapOf<Customer, Money>()

    fun totalBalance(): Money =
        accounts
            .toMap()
            .values
            .fold(Money(BigDecimal.ZERO, currency)) { totalBalance, accountBalance ->
                Money(totalBalance.value.plus(accountBalance.value), totalBalance.currency)
            }

    fun balanceFor(customer: Customer): Money? = this.accounts[customer]

    fun deposit(customer: Customer, deposit: Money): Result4k<Money, BankError> =
        validateCurrency(deposit)
            .map { validDeposit ->
                accounts[customer]?.let {
                    Money(it.value.plus(validDeposit.value), currency)
                } ?: validDeposit
            }.peek { newBalance ->
                accounts[customer] = newBalance
            }.map { deposit }


    fun withdraw(customer: Customer, withdrawingAmount: Money): Result4k<Money, BankError> =
        validateCurrency(withdrawingAmount).flatMap {
            validateWithdrawAmount(withdrawingAmount)
        }.flatMap {
            validateCustomer(customer)
        }.flatMap { existingBalance ->
            validateBalanceAgainstAttemptedWithdraw(existingBalance, withdrawingAmount, customer)
        }.map { existingBalance ->
            Money(existingBalance.value.minus(withdrawingAmount.value), currency)
        }.peek { newBalance ->
            accounts[customer] = newBalance
        }.map {
            withdrawingAmount
        }

    private fun validateCustomer(customer: Customer) = balanceFor(customer)?.let { existingBalance ->
        Success(existingBalance)
    } ?: Failure(UnknownCustomerError(customer))

    private fun validateBalanceAgainstAttemptedWithdraw(balance: Money, withdrawingAmount: Money, customer: Customer) =
        if (balance.value >= withdrawingAmount.value) {
            Success(balance)
        } else {
            Failure(
                WithdrawExceedingBalanceError(customer, withdrawingAmount)
            )
        }

    private fun validateWithdrawAmount(withdrawingAmount: Money): Result<Money, BankError> =
        if (withdrawingAmount.value <= BigDecimal.ZERO) {
            Failure(AmountHasToBeMoreThanZeroError(withdrawingAmount))
        } else {
            Success(withdrawingAmount)
        }

    private fun validateCurrency(moneyAmount: Money): Result4k<Money, BankError> =
        if (moneyAmount.currency == currency) {
            Success(moneyAmount)
        } else {
            Failure(UnSupportedCurrencyError(currency, moneyAmount.currency))
        }
}
