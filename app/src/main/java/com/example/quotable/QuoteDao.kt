package com.example.quotable

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface QuoteDao {
    @Query("SELECT * FROM quotes")
    fun getAllQuotes(): List<Quote>

    @Insert
    fun insertQuote(quote: Quote)
}
