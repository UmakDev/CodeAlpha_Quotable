package com.example.quotable

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var quoteDao: QuoteDao
    private lateinit var quotes: List<Quote>
    private lateinit var tvQuote: TextView
    private lateinit var tvAuthor: TextView
    private var currentQuoteIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)

        // Initialize views
        tvQuote = findViewById(R.id.tvQuote)
        tvAuthor = findViewById(R.id.tvAuthor)
        val btnNextQuote = findViewById<Button>(R.id.btnNextQuote)
        val btnAddQuote = findViewById<Button>(R.id.btnAddQuote)
        val btnShare = findViewById<Button>(R.id.btnShare)

        // Initialize Room Database
        val db = Room.databaseBuilder(
            applicationContext,
            QuoteDatabase::class.java, "quote_database"
        ).allowMainThreadQueries().build()
        quoteDao = db.quoteDao()

        // Load pre-saved quotes and user-added quotes
        quotes = loadPresetQuotes() + quoteDao.getAllQuotes()

        // Show a random quote initially
        showRandomQuote()

        // Button click for "Next Quote"
        btnNextQuote.setOnClickListener {
            showRandomQuote()
        }

        // Button click for "Add Quote"
        btnAddQuote.setOnClickListener {
            showAddQuoteDialog()
        }

        // Button click for "Share"
        btnShare.setOnClickListener {
            shareQuote()
        }
    }

    // Load pre-saved quotes
    private fun loadPresetQuotes(): List<Quote> {
        return listOf(
            Quote(quoteText = "The best way to predict the future is to invent it.", author = "Alan Kay"),
            Quote(quoteText = "Life is 10% what happens to us and 90% how we react to it.", author = "Charles R. Swindoll"),
            Quote(quoteText = "Your time is limited, don’t waste it living someone else’s life.", author = "Steve Jobs"),
            Quote(quoteText = "Success is not the key to happiness. Happiness is the key to success.", author = "Albert Schweitzer"),
            Quote(quoteText = "Do what you can, with what you have, where you are.", author = "Theodore Roosevelt"),
            Quote(quoteText = "Believe you can and you're halfway there.", author = "Theodore Roosevelt"),
            Quote(quoteText = "Your life does not get better by chance, it gets better by change.", author = "Jim Rohn"),
            Quote(quoteText = "The only way to do great work is to love what you do.", author = "Steve Jobs"),
            Quote(quoteText = "Success is how high you bounce when you hit bottom.", author = "George S. Patton"),
            Quote(quoteText = "I have not failed. I've just found 10,000 ways that won't work.", author = "Thomas Edison"),
            Quote(quoteText = "It always seems impossible until it's done.", author = "Nelson Mandela"),
            Quote(quoteText = "Be yourself; everyone else is already taken.", author = "Oscar Wilde"),
            Quote(quoteText = "What you get by achieving your goals is not as important as what you become by achieving your goals.", author = "Zig Ziglar"),
            Quote(quoteText = "Don’t wait. The time will never be just right.", author = "Napoleon Hill"),
            Quote(quoteText = "If you can dream it, you can do it.", author = "Walt Disney"),
            Quote(quoteText = "You miss 100% of the shots you don't take.", author = "Wayne Gretzky"),
            Quote(quoteText = "The purpose of our lives is to be happy.", author = "Dalai Lama"),
            Quote(quoteText = "Success usually comes to those who are too busy to be looking for it.", author = "Henry David Thoreau"),
            Quote(quoteText = "I find that the harder I work, the more luck I seem to have.", author = "Thomas Jefferson"),
            Quote(quoteText = "Don’t be afraid to give up the good to go for the great.", author = "John D. Rockefeller"),
            Quote(quoteText = "If you are not willing to risk the usual, you will have to settle for the ordinary.", author = "Jim Rohn"),
            Quote(quoteText = "Success is not in what you have, but who you are.", author = "Bo Bennett"),
            Quote(quoteText = "The only limit to our realization of tomorrow is our doubts of today.", author = "Franklin D. Roosevelt"),
            Quote(quoteText = "Happiness is not something ready-made. It comes from your own actions.", author = "Dalai Lama"),
            Quote(quoteText = "In the middle of difficulty lies opportunity.", author = "Albert Einstein"),
            Quote(quoteText = "It does not matter how slowly you go as long as you do not stop.", author = "Confucius"),
            Quote(quoteText = "The future belongs to those who believe in the beauty of their dreams.", author = "Eleanor Roosevelt"),
            Quote(quoteText = "Success seems to be connected with action. Successful people keep moving.", author = "Conrad Hilton"),
            Quote(quoteText = "If you want to achieve greatness stop asking for permission.", author = "Anonymous"),
            Quote(quoteText = "Do not wait to strike till the iron is hot; but make it hot by striking.", author = "William Butler Yeats")
        )
    }

    // Show a random quote from the list
    private fun showRandomQuote() {
        if (quotes.isNotEmpty()) {
            currentQuoteIndex = Random.nextInt(quotes.size)
            val randomQuote = quotes[currentQuoteIndex]
            tvQuote.text = randomQuote.quoteText
            tvAuthor.text = randomQuote.author
        }
    }

    // Add a new quote using a dialog
    private fun showAddQuoteDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_quote, null)
        val quoteEditText = dialogView.findViewById<EditText>(R.id.editQuote)
        val authorEditText = dialogView.findViewById<EditText>(R.id.editAuthor)

        AlertDialog.Builder(this)
            .setTitle("Add Quote")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val newQuote = Quote(
                    quoteText = quoteEditText.text.toString(),
                    author = authorEditText.text.toString()
                )
                quoteDao.insertQuote(newQuote)
                quotes = quotes + newQuote
                showRandomQuote()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    // Share the current quote
    private fun shareQuote() {
        val currentQuote = quotes[currentQuoteIndex]
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "${currentQuote.quoteText} — ${currentQuote.author}")
            type = "text/plain"
        }
        startActivity(Intent.createChooser(shareIntent, "Share quote via"))
    }
}
