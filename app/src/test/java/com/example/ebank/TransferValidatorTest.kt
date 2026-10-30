package com.example.ebank

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class TransferValidatorTest {

    @Test
    fun `blank account is rejected`() {
        val error = TransferValidator.validate(
            account = "", name = "Kasun Silva", amountText = "500"
        )
        assertEquals("Enter a recipient account number", error)
    }

    @Test
    fun `blank name is rejected`() {
        val error = TransferValidator.validate(
            account = "8001234567", name = "  ", amountText = "500"
        )
        assertEquals("Enter a recipient name", error)
    }

    @Test
    fun `non-numeric amount is rejected`() {
        val error = TransferValidator.validate(
            account = "8001234567", name = "Kasun Silva", amountText = "abc"
        )
        assertEquals("Enter a valid amount greater than 0", error)
    }

    @Test
    fun `zero amount is rejected`() {
        val error = TransferValidator.validate(
            account = "8001234567", name = "Kasun Silva", amountText = "0"
        )
        assertEquals("Enter a valid amount greater than 0", error)
    }

    @Test
    fun `negative amount is rejected`() {
        val error = TransferValidator.validate(
            account = "8001234567", name = "Kasun Silva", amountText = "-50"
        )
        assertEquals("Enter a valid amount greater than 0", error)
    }

    @Test
    fun `valid input passes`() {
        val error = TransferValidator.validate(
            account = "8001234567", name = "Kasun Silva", amountText = "2500.50"
        )
        assertNull(error)
    }
}
