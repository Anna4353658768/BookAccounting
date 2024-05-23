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
import com.example.books.controller.PublisherLab
import com.example.books.fragment.list.PublisherListFragment
import com.example.books.fragment.model.PublisherFragment
import com.example.books.model.Publisher
import java.util.UUID

class PublisherPagerActivity: AppCompatActivity()  {

    private lateinit var viewPager: ViewPager2
    private lateinit var publishers: List<Publisher>

    companion object {
        private const val EXTRA_PUBLISHER_ID = "com.example.books.publisher_id"
        fun newIntent(packageContext: Context?, publisherId: UUID?, position: Int) = Intent(
            packageContext,
            PublisherPagerActivity::class.java
        ).apply {
            putExtra(EXTRA_PUBLISHER_ID, publisherId)
            PublisherListFragment.POSITION = position
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publisher_pager)
        val publisherId = intent.getSerializableExtra(EXTRA_PUBLISHER_ID) as UUID?

        viewPager = findViewById(R.id.activity_publisher_pager_view_pager)
        viewPager.adapter = ViewPagerAdapter(this)

        publishers = PublisherLab.get(this).publishers
        for (i in publishers.indices)
            if (publishers[i].id == publisherId) {
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
        private val publishers: List<Publisher> = PublisherLab.get(fragmentActivity).publishers

        override fun getItemCount() = publishers.size

        override fun createFragment(position: Int) = PublisherFragment.newInstance(publishers[position].id)

    }
}