package com.example.books.fragment.list

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.books.R
import com.example.books.activity.AuthorizationActivity
import com.example.books.activity.pager.BookPagerActivity
import com.example.books.api.ApiFactory
import com.example.books.controller.BookLab
import com.example.books.controller.PublisherLab
import com.example.books.fragment.AuthorizationFragment
import com.example.books.model.Book
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

class BookListFragment : Fragment(), CoroutineScope {

    private var bookRecyclerView: RecyclerView? = null
    private var adapter: BookAdapter? = null
    private var subtitleVisible: Boolean = false

    private lateinit var progressBar: ProgressBar
    private val service = ApiFactory.bookApi
    private var loadDataFinished = false
    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main


    companion object{
        var POSITION: Int = 0
        var DELETE_INFO: Boolean = false
        private const val SAVED_SUBTITLE_VISIBLE = "subtitle"
        private const val LOAD_DATA = "loadDataFinished"
        private const val TAG = "BookListFragment"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bookLab = BookLab.get(requireActivity())
        val books = bookLab.books
        val view: View = inflater.inflate(R.layout.fragment_book_list, container, false)
        bookRecyclerView = view.findViewById(R.id.book_recycler_view)
        if(resources.configuration.orientation == 1){
            bookRecyclerView!!.layoutManager = LinearLayoutManager(activity)
        }else{
            bookRecyclerView!!.layoutManager = GridLayoutManager(activity, 2)
        }

        if (savedInstanceState != null){
            subtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE)
            loadDataFinished = savedInstanceState.getBoolean(LOAD_DATA, false)
        }

        progressBar = view.findViewById(R.id.progressBar)
        updateUI()
        if (loadDataFinished){
            updateUI()
        }
        else if(books.size == 0){
            loadData()
        }

        return view
    }


    override fun onResume() {
        super.onResume()
        updateUI()
    }


    private fun updateUI() {
        val bookLab = BookLab.get(requireActivity())
        val books = bookLab.books

        if (books.size == 0) {
            val emptyNewBookButton = view?.findViewById<TextView>(R.id.empty_new_book)
            emptyNewBookButton?.visibility = View.VISIBLE
            view?.findViewById<TextView>(R.id.empty_view)?.visibility = View.VISIBLE
            bookRecyclerView?.visibility = View.GONE

            emptyNewBookButton?.setOnClickListener {
                val book = Book()
                BookLab.get(requireActivity()).addBook(book)
                val intent = BookPagerActivity.newIntent(requireActivity(), book.id, 0)
                startActivity(intent)
            }

        }
        else{
            view?.findViewById<TextView>(R.id.empty_view)?.visibility = View.GONE
            view?.findViewById<TextView>(R.id.empty_new_book)?.visibility = View.GONE
            bookRecyclerView?.visibility = View.VISIBLE

            if (adapter == null) {
                adapter = BookAdapter(books)
                bookRecyclerView!!.adapter = adapter
            }
            else {
                if(DELETE_INFO){
                    adapter!!.notifyItemRemoved(POSITION)
                    adapter!!.notifyItemRangeChanged(POSITION, books.size)
                    DELETE_INFO = false
                }
                else{
                    adapter!!.notifyItemChanged(POSITION)
                }
            }
        }
        updateSubtitle()
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_book_list, menu)
        val subtitleItem = menu.findItem(R.id.menu_item_show_subtitle)
        if (subtitleVisible)
            subtitleItem.setTitle(R.string.hide_subtitle)
        else
            subtitleItem.setTitle(R.string.show_subtitle)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            R.id.menu_item_new_book -> {
                val bookLab = BookLab.get(requireActivity())
                val books = bookLab.books
                val book = Book()
                BookLab.get(requireActivity()).addBook(book)
                val intent = BookPagerActivity.newIntent(requireActivity(), book.id, books.size - 1)
                postBook(book)
                startActivity(intent)
                true
            }
            R.id.menu_item_show_subtitle -> {
                subtitleVisible = !subtitleVisible
                requireActivity().invalidateOptionsMenu()
                updateSubtitle()
                true
            }
            R.id.menu_item_exit -> {
                AuthorizationFragment.USER_TOKEN?.token = ""
                val intent = Intent(requireContext(), AuthorizationActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(SAVED_SUBTITLE_VISIBLE, subtitleVisible)
        outState.putBoolean(LOAD_DATA, loadDataFinished)
    }


    private fun updateSubtitle() {
        val bookCount = BookLab.get(requireActivity()).books.size
        var subtitle = resources.getQuantityString(R.plurals.subtitle_plural_book, bookCount, bookCount)

        if (!subtitleVisible){
            subtitle = ""
        }
        val activity = activity as AppCompatActivity?
        activity!!.supportActionBar!!.subtitle = subtitle
    }


    override fun onDestroy() {
        super.onDestroy()
        coroutineContext.cancelChildren()
    }


    private fun loadData() = runBlocking {
        val job = launch {
            progressBar.visibility = View.VISIBLE
            val bookLab = BookLab.get(requireActivity())
            val postRequest = service.getBooks()
            try {
                val response = postRequest.await()
                if (response.isSuccessful) {
                    bookLab.books = response.body() as MutableList<Book>
                    Log.d(TAG, "Успешная загрузка книг")
                }
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
            } finally {
                updateUI()
                progressBar.visibility = View.GONE
            }
        }
        job.join()
        loadDataFinished = true
    }


    private fun postBook(book: Book) = runBlocking {
        val job = launch {
            val request = service.postBook(book)
            try {
                val response = request.await()
                if (response.isSuccessful) {
                    Log.d(TAG, "Успешное добавление новой книги")
                }
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
            }
        }
        job.join()
    }

}


private class BookHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!), View.OnClickListener {
    private var titleTextView: TextView =  itemView!!.findViewById(R.id.list_item_book_title)
    private var authorTextView: TextView = itemView!!.findViewById(R.id.list_item_book_author)
    private var publisherTextView: TextView = itemView!!.findViewById(R.id.list_item_book_publisher)
    private var yearTextView: TextView = itemView!!.findViewById(R.id.list_item_book_year)
    private var codeTextView: TextView = itemView!!.findViewById(R.id.list_item_book_code)
    private var statusCheckBox: TextView = itemView!!.findViewById(R.id.list_item_book_status)
    private lateinit var book: Book

    fun bindBook(book: Book) {
        this.book = book
        titleTextView.text = book.title
        publisherTextView.text = book.publisher?.name
        codeTextView.text = book.code
        yearTextView.text = book.yearPublish


        var fullName = ""
        if(book.author?.surname != null){
            fullName += book.author?.surname + " "
        }
        if(book.author?.name != null){
            fullName += book.author?.name  + " "
        }
        if(book.author?.patronymic != null){
            fullName += book.author?.patronymic
        }
        authorTextView.text = fullName


        if(book.status){
            statusCheckBox.setTextColor(Color.parseColor("#FF388E3C"))
            statusCheckBox.text = "Есть в наличии"
        }
        else{
            statusCheckBox.setTextColor(Color.parseColor("#D32F2F"))
            statusCheckBox.text = "Нет в наличии"
        }

        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val context = v!!.context
        val intent = BookPagerActivity.newIntent(context, book.id, absoluteAdapterPosition)
        context.startActivity(intent)
    }

}


private class BookAdapter(books: List<Book>?) : RecyclerView.Adapter<BookHolder?>() {

    private var books: List<Book>? = null

    init {
        this.books = books
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item_book, parent, false)
        return BookHolder(view)
    }

    override fun getItemCount(): Int {
        return books!!.size
    }

    override fun onBindViewHolder(holder: BookHolder, position: Int) {
        val book = books!![position]
        holder.bindBook(book)
    }

}