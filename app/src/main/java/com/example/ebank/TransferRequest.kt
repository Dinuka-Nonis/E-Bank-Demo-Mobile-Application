package com.example.ebank

import java.io.Serializable

data class TransferRequest(
    val recipientAccount: String,
    val recipientName: String,
    val amount: Double,
    val remarks: String
) : Serializable