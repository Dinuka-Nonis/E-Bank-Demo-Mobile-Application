package com.example.ebank

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface TransferDao {

    @Insert
    suspend fun insert(request: TransferRequest)

    @Query("SELECT * FROM transfer_history ORDER BY id DESC")
    suspend fun getAll(): List<TransferRequest>
}
