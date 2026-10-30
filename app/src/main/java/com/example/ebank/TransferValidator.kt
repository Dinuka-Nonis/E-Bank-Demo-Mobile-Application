package com.example.ebank

/**
 * Lab 8: the transfer form's input rules as a plain Kotlin object.
 * No Fragment, View, Binding or Context, so it runs as a fast local JUnit test.
 */
object TransferValidator {

    /**
     * Returns null if the input is valid, or an error message naming
     * the first problem found, in the same order onSubmitTransfer()
     * checked them.
     */
    fun validate(
        account: String,
        name: String,
        amountText: String
    ): String? {
        if (account.isBlank()) {
            return "Enter a recipient account number"
        }
        if (name.isBlank()) {
            return "Enter a recipient name"
        }
        // The amount field groups digits with commas ("15,000.00"), so strip them first.
        val amount = amountText.replace(",", "").toDoubleOrNull()
        if (amount == null || !amount.isFinite() || amount <= 0.0) {
            return "Enter a valid amount greater than 0"
        }
        return null
    }
}
