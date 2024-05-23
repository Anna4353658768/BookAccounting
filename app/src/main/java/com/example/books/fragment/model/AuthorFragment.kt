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
import com.example.books.fragment.list.AuthorListFragment
import com.example.books.model.Author
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.UUID


class AuthorFragment : Fragment()  {

    private var author: Author? = null
    private lateinit var nameField: EditText
    private lateinit var surnameField: EditText
    private lateinit var patronymicField: EditText
    private lateinit var deleteButton: Button
    private var createAuthor: Author = Author()
    private val service = ApiFactory.authorApi

    companion object {
        private const val ARG_AUTHOR_ID = "author_id"
        private const val TAG = "AuthorFragment"

        fun newInstance(authorId: UUID?) = AuthorFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_AUTHOR_ID, authorId)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super. onCreate(savedInstanceState)
        val authorId = requireArguments().getSerializable(ARG_AUTHOR_ID) as UUID?
        author = AuthorLab.get(requireActivity()).getAuthor(authorId as UUID)
        changeAuthor()
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.fragment_author, container, false)

        nameField = v.findViewById(R.id.author_name)
        nameField.setText(author?.name)
        nameField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int) {
                author?.name = s.toString()
            }

            override fun afterTextChanged(c: Editable) {
            }
        })


        surnameField = v.findViewById(R.id.author_surname)
        surnameField.setText(author?.surname)
        surnameField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int) {
                author?.surname = s.toString()
            }

            override fun afterTextChanged(c: Editable) {
            }
        })


        patronymicField = v.findViewById(R.id.author_patronymic)
        patronymicField.setText(author?.patronymic)
        patronymicField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int) {
                author?.patronymic = s.toString()
            }

            override fun afterTextChanged(c: Editable) {
            }
        })


        deleteButton = v.findViewById(R.id.author_delete_button)
        deleteButton.setOnClickListener {
            val authorId = requireArguments().getSerializable(ARG_AUTHOR_ID) as UUID?
            author = AuthorLab.get(requireActivity()).getAuthor(authorId as UUID)
            val bookLab = BookLab.get(requireActivity())
            val books = bookLab.books
            var flag = false
            for(i in books){
                if(i.author?.id == author?.id){
                    flag = true
                    break
                }
            }
            if (!flag){
                AuthorLab.get(requireActivity()).deleteAuthor(author!!)
                AuthorListFragment.DELETE_INFO = true
                deleteAuthorById(authorId)
                activity?.finish()
            }
            else{
                val text = "Этот автор привязан к книге!"
                Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
            }
        }
        return v
    }


    override fun onPause() {
        if(!AuthorListFragment.DELETE_INFO){
            if(createAuthor.name != author?.name
                || createAuthor.surname != author?.surname
                || createAuthor.patronymic != author?.patronymic)
            {
                putAuthor(author!!)
                changeAuthor()
            }
        }
        super.onPause()
    }


    private fun changeAuthor(){
        createAuthor.name = author?.name.toString()
        createAuthor.surname = author?.surname.toString()
        createAuthor.patronymic = author?.patronymic.toString()
    }


    private fun deleteAuthorById(authorId: UUID) = runBlocking {
        val job = launch {
            val request = service.deleteAuthorById(authorId)
            try {
                val response = request.await()
                if (response.isSuccessful) {
                    Log.d(TAG, "Успешное удаление издателя $authorId")
                }
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
            }
        }
        job.join()
    }


    private fun putAuthor(author: Author) = runBlocking {
        val job = launch {
            val request = service.putAuthor(author)
            try {
                val response = request.await()
                if (response.isSuccessful) {
                    Log.d(TAG, "Успешное изменение издателя ${author.id}")
                }
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
            }
        }
        job.join()
    }

}