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
import com.example.books.R
import com.example.books.controller.AuthorLab
import com.example.books.fragment.list.AuthorListFragment
import com.example.books.fragment.model.AuthorFragment
import com.example.books.model.Author
import java.util.UUID

class AuthorPagerActivity: AppCompatActivity()  {

    private lateinit var viewPager: ViewPager2
    private lateinit var authors: List<Author>

    companion object {
        private const val EXTRA_AUTHOR_ID = "com.example.books.author_id"
        fun newIntent(packageContext: Context?, authorId: UUID?, position: Int) = Intent(
            packageContext,
            AuthorPagerActivity::class.java
        ).apply {
            putExtra(EXTRA_AUTHOR_ID, authorId)
            AuthorListFragment.POSITION = position
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_author_pager)
        val authorId = intent.getSerializableExtra(EXTRA_AUTHOR_ID) as UUID?

        viewPager = findViewById(R.id.activity_author_pager_view_pager)
        viewPager.adapter = ViewPagerAdapter(this)

        authors = AuthorLab.get(this).authors
        for (i in authors.indices)
            if (authors[i].id == authorId) {
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
        private val authors: List<Author> = AuthorLab.get(fragmentActivity).authors

        override fun getItemCount() = authors.size

        override fun createFragment(position: Int) = AuthorFragment.newInstance(authors[position].id)

    }

}