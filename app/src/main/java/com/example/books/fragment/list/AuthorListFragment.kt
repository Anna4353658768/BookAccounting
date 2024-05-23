package com.example.books.fragment.list

import android.content.Intent
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
import com.example.books.activity.pager.AuthorPagerActivity
import com.example.books.api.ApiFactory
import com.example.books.controller.AuthorLab
import com.example.books.controller.BookLab
import com.example.books.controller.PublisherLab
import com.example.books.fragment.AuthorizationFragment
import com.example.books.model.Author
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

class AuthorListFragment : Fragment(), CoroutineScope {

    private var authorRecyclerView: RecyclerView? = null
    private var adapter: AuthorAdapter? = null
    private var subtitleVisible: Boolean = false

    private lateinit var progressBar: ProgressBar
    private val service = ApiFactory.authorApi
    private var loadDataFinished = false
    private val job = SupervisorJob()


    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main


    companion object{
        var POSITION: Int = 0
        var DELETE_INFO: Boolean = false
        private const val SAVED_SUBTITLE_VISIBLE = "subtitle"
        private const val LOAD_DATA = "loadDataFinished"
        private const val TAG = "AuthorListFragment"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val authorLab = AuthorLab.get(requireActivity())
        val authors = authorLab.authors
        val view: View = inflater.inflate(R.layout.fragment_author_list, container, false)
        authorRecyclerView = view.findViewById(R.id.author_recycler_view)
        if(resources.configuration.orientation == 1){
            authorRecyclerView!!.layoutManager = LinearLayoutManager(activity)
        }else{
            authorRecyclerView!!.layoutManager = GridLayoutManager(activity, 2)
        }

        if (savedInstanceState != null){
            subtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE)
            loadDataFinished = savedInstanceState.getBoolean(LOAD_DATA, false)
        }

        progressBar = view.findViewById(R.id.progressBar_author)
        updateUI()

        if (loadDataFinished){
            updateUI()
        }
        else if(authors.size == 0){
            loadData()
        }
        return view
    }


    override fun onResume() {
        super.onResume()
        updateUI()
    }


    private fun updateUI() {
        val authorLab = AuthorLab.get(requireActivity())
        val authors = authorLab.authors

        if (authors.size == 0) {
            val emptyNewAuthorButton = view?.findViewById<TextView>(R.id.empty_new_author)
            emptyNewAuthorButton?.visibility = View.VISIBLE
            view?.findViewById<TextView>(R.id.empty_view_author)?.visibility = View.VISIBLE
            authorRecyclerView?.visibility = View.GONE

            emptyNewAuthorButton?.setOnClickListener {
                val author = Author()
                AuthorLab.get(requireActivity()).addAuthor(author)
                val intent = AuthorPagerActivity.newIntent(requireActivity(), author.id, 0)
                startActivity(intent)
            }

        }
        else{
            view?.findViewById<TextView>(R.id.empty_view_author)?.visibility = View.GONE
            view?.findViewById<TextView>(R.id.empty_new_author)?.visibility = View.GONE
            authorRecyclerView?.visibility = View.VISIBLE

            if (adapter == null) {
                adapter = AuthorAdapter(authors)
                authorRecyclerView!!.adapter = adapter
            }
            else {
                if(DELETE_INFO){
                    adapter!!.notifyItemRemoved(POSITION)
                    adapter!!.notifyItemRangeChanged(POSITION, authors.size)
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
                val authorLab = AuthorLab.get(requireActivity())
                val authors = authorLab.authors
                val author = Author()
                AuthorLab.get(requireActivity()).addAuthor(author)
                val intent = AuthorPagerActivity.newIntent(requireActivity(), author.id, authors.size - 1)
                postAuthor(author)
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
        val authorCount = AuthorLab.get(requireActivity()).authors.size

        var subtitle = resources.getQuantityString(R.plurals.subtitle_plural_author, authorCount, authorCount)

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

            val authorLab = AuthorLab.get(requireActivity())

            val postRequest = service.getAuthors()
            try {
                val response = postRequest.await()
                if (response.isSuccessful) {
                    authorLab.authors = response.body() as MutableList<Author>
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


    private fun postAuthor(author: Author) = runBlocking {
        val job = launch {
            val request = service.postAuthor(author)
            try {
                val response = request.await()
                if (response.isSuccessful) {
                    Log.d(TAG, "Успешное добавление нового автора")
                }
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
            }
        }
        job.join()
    }

}


private fun getAuthorFullName(author: Author): String {
    var fullName = ""
    if(author.surname != ""){
        fullName += author.surname + " "
    }
    if(author.name != ""){
        fullName += author.name + " "
    }
    if(author.patronymic != ""){
        fullName += author.patronymic
    }
    return fullName
}


private class AuthorHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!), View.OnClickListener {
    private var fullNameTextView: TextView = itemView!!.findViewById(R.id.list_item_author_full_name)
    private lateinit var author: Author

    fun bindAuthor(author: Author) {
        this.author = author

        fullNameTextView.text = getAuthorFullName(author)

        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val context = v!!.context
        val intent = AuthorPagerActivity.newIntent(context, author.id, absoluteAdapterPosition)
        context.startActivity(intent)
    }

}


private class AuthorAdapter(authors: List<Author>?) : RecyclerView.Adapter<AuthorHolder?>() {

    private var authors: List<Author>? = null

    init {
        this.authors = authors
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AuthorHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item_author, parent, false)
        return AuthorHolder(view)
    }

    override fun getItemCount(): Int {
        return authors!!.size
    }

    override fun onBindViewHolder(holder: AuthorHolder, position: Int) {
        val author = authors!![position]
        holder.bindAuthor(author)
    }

}