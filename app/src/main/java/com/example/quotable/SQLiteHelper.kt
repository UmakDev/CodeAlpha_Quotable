package com.example.quotable.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLiteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_VERSION = 2
        private const val DATABASE_NAME = "QuotesDB"
        private const val TABLE_QUOTES = "Quotes"
        private const val KEY_ID = "id"
        private const val KEY_QUOTE = "quote"
        private const val KEY_AUTHOR = "author"
    }

    private var displayedQuotes = mutableListOf<Quote>()

    override fun onCreate(db: SQLiteDatabase?) {
        val createTable = ("CREATE TABLE $TABLE_QUOTES ("
                + "$KEY_ID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "$KEY_QUOTE TEXT,"
                + "$KEY_AUTHOR TEXT)")
        db?.execSQL(createTable)

        // Insert default quotes
        insertDefaultQuotes(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_QUOTES")
        onCreate(db)
    }

    private fun insertDefaultQuotes(db: SQLiteDatabase?) {
        val quotes = listOf(
            "The only limit to our realization of tomorrow is our doubts of today." to "Franklin D. Roosevelt",
            "Success is not final, failure is not fatal: It is the courage to continue that counts." to "Winston Churchill",
            "What we think, we become." to "Buddha",
            "The best time to plant a tree was 20 years ago. The second best time is now." to "Chinese Proverb",
            "You miss 100% of the shots you don’t take." to "Wayne Gretzky",
            "It’s not whether you get knocked down, it’s whether you get up." to "Vince Lombardi",
            "The future belongs to those who believe in the beauty of their dreams." to "Eleanor Roosevelt",
            "Don’t watch the clock; do what it does. Keep going." to "Sam Levenson",
            "The way to get started is to quit talking and begin doing." to "Walt Disney",
            "Life is 10% what happens to us and 90% how we react to it." to "Charles R. Swindoll",
            "You only live once, but if you do it right, once is enough." to "Mae West",
            "If you can dream it, you can do it." to "Walt Disney",
            "Act as if what you do makes a difference. It does." to "William James",
            "Success usually comes to those who are too busy to be looking for it." to "Henry David Thoreau",
            "Opportunities don't happen. You create them." to "Chris Grosser",
            "Don’t be pushed around by the fears in your mind. Be led by the dreams in your heart." to "Roy T. Bennett",
            "The only way to do great work is to love what you do." to "Steve Jobs",
            "The only person you are destined to become is the person you decide to be." to "Ralph Waldo Emerson",
            "Believe you can and you’re halfway there." to "Theodore Roosevelt",
            "Your time is limited, so don’t waste it living someone else’s life." to "Steve Jobs",
            "Life is what happens when you’re busy making other plans." to "John Lennon",
            "To live is the rarest thing in the world. Most people exist, that is all." to "Oscar Wilde",
            "Success is not how high you have climbed, but how you make a positive difference to the world." to "Roy T. Bennett",
            "What lies behind us and what lies before us are tiny matters compared to what lies within us." to "Ralph Waldo Emerson",
            "You have within you right now, everything you need to deal with whatever the world can throw at you." to "Brian Tracy",
            "The only limit to our realization of tomorrow will be our doubts of today." to "Franklin D. Roosevelt",
            "The best revenge is massive success." to "Frank Sinatra",
            "Life is not measured by the number of breaths we take, but by the moments that take our breath away." to "Maya Angelou",
            "Success is not in what you have, but who you are." to "Bo Bennett",
            "What you get by achieving your goals is not as important as what you become by achieving your goals." to "Zig Ziglar",
            "Dream big and dare to fail." to "Norman Vaughan",
            "It is during our darkest moments that we must focus to see the light." to "Aristotle",
            "You cannot swim for new horizons until you have courage to lose sight of the shore." to "William Faulkner",
            "The secret of getting ahead is getting started." to "Mark Twain",
            "Success is walking from failure to failure with no loss of enthusiasm." to "Winston Churchill",
            "In the end, we will remember not the words of our enemies, but the silence of our friends." to "Martin Luther King Jr.",
            "The future belongs to those who prepare for it today." to "Malcolm X",
            "It does not matter how slowly you go as long as you do not stop." to "Confucius",
            "Keep your face always toward the sunshine—and shadows will fall behind you." to "Walt Whitman",
            "Success is not just about what you accomplish in your life, it’s about what you inspire others to do." to "Unknown",
            "You must be the change you wish to see in the world." to "Mahatma Gandhi",
            "Failure is simply the opportunity to begin again, this time more intelligently." to "Henry Ford",
            "What you do makes a difference, and you have to decide what kind of difference you want to make." to "Jane Goodall",
            "The best way to predict the future is to create it." to "Peter Drucker",
            "Believe in yourself and all that you are. Know that there is something inside you that is greater than any obstacle." to "Christian D. Larson",
            "To succeed in life, you need three things: a wishbone, a backbone, and a funny bone." to "Reba McEntire",
            "The mind is everything. What you think you become." to "Buddha"
        )

        quotes.forEach { (quote, author) -> insertQuote(db, quote, author) }
    }

    fun insertQuote(db: SQLiteDatabase?, quote: String, author: String) {
        val contentValues = ContentValues().apply {
            put(KEY_QUOTE, quote)
            put(KEY_AUTHOR, author)
        }
        db?.insert(TABLE_QUOTES, null, contentValues)
    }

    fun addQuote(quote: String, author: String): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(KEY_QUOTE, quote)
            put(KEY_AUTHOR, author)
        }
        return db.insert(TABLE_QUOTES, null, contentValues)
    }

    fun getRandomQuote(): Quote? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_QUOTES", null)

        if (cursor.count == 0) {
            cursor.close()
            return null
        }

        val randomIndex = (0 until cursor.count).random()
        cursor.moveToPosition(randomIndex)

        val quote = cursor.getString(cursor.getColumnIndexOrThrow(KEY_QUOTE))
        val author = cursor.getString(cursor.getColumnIndexOrThrow(KEY_AUTHOR))
        cursor.close()

        val newQuote = Quote(quote, author)

        // Ensure that the same quote is not displayed again
        if (!displayedQuotes.contains(newQuote)) {
            displayedQuotes.add(newQuote)
        } else {
            return getRandomQuote() // Try again if it has been displayed
        }

        return newQuote
    }

    fun resetDisplayedQuotes() {
        displayedQuotes.clear() // Call this when you want to reset the displayed quotes
    }
}

data class Quote(val text: String, val author: String)
