package com.example.books.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.books.R
import com.example.books.activity.pager.AuthorPagerActivity
import com.example.books.activity.pager.PublisherPagerActivity
import com.example.books.controller.AuthorLab
import com.example.books.fragment.AuthorizationFragment
import com.example.books.fragment.RegistrationFragment
import com.example.books.fragment.list.PublisherListFragment
import java.util.UUID

class RegistrationActivity : AppCompatActivity() {

    companion object {
        fun newIntent(packageContext: Context?) = Intent(
            packageContext, RegistrationActivity::class.java
        ).apply {
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_login)

        val registrationFragment = RegistrationFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_login, registrationFragment)
            .commit()
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

}