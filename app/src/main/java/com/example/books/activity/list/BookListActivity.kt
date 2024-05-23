package com.example.books.activity.list

import androidx.fragment.app.Fragment
import com.example.books.fragment.list.BookListFragment
import com.example.books.activity.SingleFragmentActivity

class BookListActivity : SingleFragmentActivity() {

    override fun createFragment(): Fragment = BookListFragment()

}