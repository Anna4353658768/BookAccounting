package com.example.books.activity.pager

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.books.fragment.list.BookListFragment
import com.example.books.R
import com.example.books.controller.BookLab
import com.example.books.fragment.model.BookFragment
import com.example.books.model.Book
import java.util.UUID

class BookPagerActivity: AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var books: List<Book>

    companion object {
        private const val EXTRA_BOOK_ID = "com.example.books.book_id"
        fun newIntent(packageContext: Context?, bookId: UUID?, position: Int) = Intent(
            packageContext,
            BookPagerActivity::class.java
        ).apply {
            putExtra(EXTRA_BOOK_ID, bookId)
            BookListFragment.POSITION = position
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_pager)
        val bookId = intent.getSerializableExtra(EXTRA_BOOK_ID) as UUID?

        viewPager = findViewById(R.id.activity_book_pager_view_pager)
        viewPager.adapter = ViewPagerAdapter(this)

        books = BookLab.get(this).books
        for (i in books.indices)
            if (books[i].id == bookId) {
                viewPager.currentItem = i
                break
            }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        private val books: List<Book> = BookLab.get(fragmentActivity).books

        override fun getItemCount() = books.size

        override fun createFragment(position: Int) = BookFragment.newInstance(books[position].id)

    }

}