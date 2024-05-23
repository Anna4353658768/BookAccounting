package com.example.books.activity.list

import androidx.fragment.app.Fragment
import com.example.books.activity.SingleFragmentActivity
import com.example.books.fragment.list.AuthorListFragment

class AuthorListActivity : SingleFragmentActivity()  {

    override fun createFragment(): Fragment = AuthorListFragment()

}