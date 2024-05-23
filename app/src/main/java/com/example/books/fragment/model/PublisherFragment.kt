package com.example.books.fragment.model

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.books.R
import com.example.books.api.ApiFactory
import com.example.books.controller.AuthorLab
import com.example.books.controller.BookLab
import com.example.books.controller.PublisherLab
import com.example.books.fragment.list.AuthorListFragment
import com.example.books.fragment.list.PublisherListFragment
import com.example.books.model.Publisher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.UUID

class PublisherFragment : Fragment()  {

    private var publisher: Publisher? = null
    private lateinit var nameField: EditText
    private lateinit var addressField: EditText
    private lateinit var siteField: EditText
    private lateinit var deleteButton: Button
    private var createPublisher: Publisher = Publisher()
    private val service = ApiFactory.publisherApi

    companion object {
        private const val ARG_PUBLISHER_ID = "publisher_id"
        private const val TAG = "PublisherFragment"

        fun newInstance(publisherId: UUID?) = PublisherFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_PUBLISHER_ID, publisherId)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super. onCreate(savedInstanceState)
        val publisherId = requireArguments().getSerializable(ARG_PUBLISHER_ID) as UUID?
        publisher = PublisherLab.get(requireActivity()).getPublisher(publisherId as UUID)
        changePublisher()
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.fragment_publisher, container, false)

        nameField = v.findViewById(R.id.publisher_name)
        nameField.setText(publisher?.name)
        nameField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int) {
                publisher?.name = s.toString()
            }

            override fun afterTextChanged(c: Editable) {
            }
        })


        addressField = v.findViewById(R.id.publisher_address)
        addressField.setText(publisher?.address)
        addressField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int) {
                publisher?.address = s.toString()
            }

            override fun afterTextChanged(c: Editable) {
            }
        })


        siteField = v.findViewById(R.id.publisher_site)
        siteField.setText(publisher?.site)
        siteField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int) {
                publisher?.site = s.toString()
            }

            override fun afterTextChanged(c: Editable) {
            }
        })


        deleteButton = v.findViewById(R.id.publisher_delete_button)
        deleteButton.setOnClickListener {
            val publisherId = requireArguments().getSerializable(ARG_PUBLISHER_ID) as UUID?
            publisher = PublisherLab.get(requireActivity()).getPublisher(publisherId as UUID)
            val bookLab = BookLab.get(requireActivity())
            val books = bookLab.books
            var flag = false
            for(i in books){
                if(i.publisher?.id == publisher?.id){
                    flag = true
                    break
                }
            }
            if (!flag){
                PublisherLab.get(requireActivity()).deletePublisher(publisher!!)
                PublisherListFragment.DELETE_INFO = true
                deletePublisherById(publisherId)
                activity?.finish()
            }
            else{
                val text = "Этот издатель привязан к книге!"
                Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
            }
        }
        return v
    }


    override fun onPause() {
        if(!PublisherListFragment.DELETE_INFO){
            if(createPublisher.name != publisher?.name
                || createPublisher.site != publisher?.site
                || createPublisher.address != publisher?.address)
            {
                putPublisher(publisher!!)
                changePublisher()
            }
        }
        super.onPause()
    }


    private fun changePublisher(){
        createPublisher.name = publisher?.name.toString()
        createPublisher.address = publisher?.address.toString()
        createPublisher.site = publisher?.site.toString()
    }


    private fun deletePublisherById(publisherId: UUID) = runBlocking {
        val job = launch {
            val request = service.deletePublisherById(publisherId)
            try {
                val response = request.await()
                if (response.isSuccessful) {
                    Log.d(TAG, "Успешное удаление издателя $publisherId")
                }
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
            }
        }
        job.join()
    }


    private fun putPublisher(publisher: Publisher) = runBlocking {
        val job = launch {
            val request = service.putPublisher(publisher)
            try {
                val response = request.await()
                if (response.isSuccessful) {
                    Log.d(TAG, "Успешное изменение издателя ${publisher.id}")
                }
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
            }
        }
        job.join()
    }

}