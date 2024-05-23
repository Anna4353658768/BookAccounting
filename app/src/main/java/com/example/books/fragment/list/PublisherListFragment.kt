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
import com.example.books.activity.pager.PublisherPagerActivity
import com.example.books.api.ApiFactory
import com.example.books.controller.PublisherLab
import com.example.books.fragment.AuthorizationFragment
import com.example.books.model.Publisher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext

class PublisherListFragment : Fragment(), CoroutineScope {

    private var publisherRecyclerView: RecyclerView? = null
    private var adapter: PublisherAdapter? = null
    private var subtitleVisible: Boolean = false

    private lateinit var progressBar: ProgressBar
    private val service = ApiFactory.publisherApi
    private var loadDataFinished = false
    private val job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = job + Dispatchers.Main


    companion object{
        var POSITION: Int = 0
        var DELETE_INFO: Boolean = false
        private const val SAVED_SUBTITLE_VISIBLE = "subtitle"
        private const val LOAD_DATA = "loadDataFinished"
        private const val TAG = "PublisherListFragment"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val publisherLab = PublisherLab.get(requireActivity())
        val publishers = publisherLab.publishers
        val view: View = inflater.inflate(R.layout.fragment_publisher_list, container, false)
        publisherRecyclerView = view.findViewById(R.id.publisher_recycler_view)
        if(resources.configuration.orientation == 1){
            publisherRecyclerView!!.layoutManager = LinearLayoutManager(activity)
        }else{
            publisherRecyclerView!!.layoutManager = GridLayoutManager(activity, 2)
        }

        if (savedInstanceState != null){
            subtitleVisible = savedInstanceState.getBoolean(SAVED_SUBTITLE_VISIBLE)
            loadDataFinished = savedInstanceState.getBoolean(LOAD_DATA, false)
        }

        progressBar = view.findViewById(R.id.progressBar_publisher)
        updateUI()
        if (loadDataFinished){
            updateUI()
        }
        else if(publishers.size == 0){
            loadData()
        }

        return view
    }


    override fun onResume() {
        super.onResume()
        updateUI()
    }


    private fun updateUI() {
        val publisherLab = PublisherLab.get(requireActivity())
        val publishers = publisherLab.publishers

        if (publishers.size == 0) {
            val emptyNewPublisherButton = view?.findViewById<TextView>(R.id.empty_new_publisher)
            emptyNewPublisherButton?.visibility = View.VISIBLE
            view?.findViewById<TextView>(R.id.empty_view_publisher)?.visibility = View.VISIBLE
            publisherRecyclerView?.visibility = View.GONE

            emptyNewPublisherButton?.setOnClickListener {
                val publisher = Publisher()
                PublisherLab.get(requireActivity()).addPublisher(publisher)
                val intent = PublisherPagerActivity.newIntent(requireActivity(), publisher.id, 0)
                startActivity(intent)
            }

        }
        else{
            view?.findViewById<TextView>(R.id.empty_view_publisher)?.visibility = View.GONE
            view?.findViewById<TextView>(R.id.empty_new_publisher)?.visibility = View.GONE
            publisherRecyclerView?.visibility = View.VISIBLE

            if (adapter == null) {
                adapter = PublisherAdapter(publishers)
                publisherRecyclerView!!.adapter = adapter
            }
            else {
                if(DELETE_INFO){
                    adapter!!.notifyItemRemoved(POSITION)
                    adapter!!.notifyItemRangeChanged(POSITION, publishers.size)
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
                val publisherLab = PublisherLab.get(requireActivity())
                val publishers = publisherLab.publishers
                val publisher = Publisher()
                PublisherLab.get(requireActivity()).addPublisher(publisher)
                val intent = PublisherPagerActivity.newIntent(requireActivity(), publisher.id, publishers.size - 1)
                postPublisher(publisher)
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
        val publisherCount = PublisherLab.get(requireActivity()).publishers.size

        var subtitle = resources.getQuantityString(R.plurals.subtitle_plural_publisher, publisherCount, publisherCount)

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

            val publisherLab = PublisherLab.get(requireActivity())

            val postRequest = service.getPublishers()
            try {
                val response = postRequest.await()
                if (response.isSuccessful) {
                    publisherLab.publishers = response.body() as MutableList<Publisher>
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


    private fun postPublisher(publisher: Publisher) = runBlocking {
        val job = launch {
            val request = service.postPublisher(publisher)
            try {
                val response = request.await()
                if (response.isSuccessful) {
                    Log.d(TAG, "Успешное добавление нового издателя")
                }
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
            }
        }
        job.join()
    }

}


private class PublisherHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!), View.OnClickListener {
    private var nameTextView: TextView =  itemView!!.findViewById(R.id.list_item_publisher_name)
    private var addressTextView: TextView = itemView!!.findViewById(R.id.list_item_publisher_address)
    private var siteTextView: TextView = itemView!!.findViewById(R.id.list_item_publisher_site)
    private lateinit var publisher: Publisher

    fun bindPublisher(publisher: Publisher) {
        this.publisher = publisher

        nameTextView.text = publisher.name
        addressTextView.text = publisher.address
        siteTextView.text = publisher.site

        itemView.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        val context = v!!.context
        val intent = PublisherPagerActivity.newIntent(context, publisher.id, absoluteAdapterPosition)
        context.startActivity(intent)
    }

}


private class PublisherAdapter(publishers: List<Publisher>?) : RecyclerView.Adapter<PublisherHolder?>() {

    private var publishers: List<Publisher>? = null

    init {
        this.publishers = publishers
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PublisherHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.list_item_publisher, parent, false)
        return PublisherHolder(view)
    }

    override fun getItemCount(): Int {
        return publishers!!.size
    }

    override fun onBindViewHolder(holder: PublisherHolder, position: Int) {
        val publisher = publishers!![position]
        holder.bindPublisher(publisher)
    }

}