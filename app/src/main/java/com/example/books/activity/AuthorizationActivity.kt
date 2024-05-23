package com.example.books.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.books.R
import com.example.books.fragment.AuthorizationFragment
import com.example.books.fragment.RegistrationFragment

class AuthorizationActivity : AppCompatActivity() {

    companion object {
        fun newIntent(packageContext: Context?) = Intent(
            packageContext, AuthorizationActivity::class.java
        ).apply {
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_login)

        val authorizationFragment = AuthorizationFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_login, authorizationFragment)
            .commit()
    }

}
