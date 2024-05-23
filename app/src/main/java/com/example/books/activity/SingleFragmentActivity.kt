package com.example.books.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.books.R
import com.example.books.databinding.ActivityFragmentBinding
import com.example.books.fragment.list.AuthorListFragment
import com.example.books.fragment.list.BookListFragment
import com.example.books.fragment.list.PublisherListFragment

abstract class SingleFragmentActivity: AppCompatActivity() {

    private lateinit var binding: ActivityFragmentBinding

    protected abstract fun createFragment(): Fragment

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment)

        val fm = supportFragmentManager
        var fragment = fm.findFragmentById(R.id.fragmentContainer)
        if (fragment == null){
            fragment = createFragment()
            fm.beginTransaction()
                .add(R.id.fragmentContainer, fragment)
                .commit()
        }


        binding = ActivityFragmentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.bottomNavigationView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_item_list_book -> replaceFragment(BookListFragment())
                R.id.menu_item_list_author -> replaceFragment(AuthorListFragment())
                R.id.menu_item_list_publisher -> replaceFragment(PublisherListFragment())
            }
            true
        }
    }


    private fun replaceFragment(newFragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, newFragment)
            .commit()
    }
}