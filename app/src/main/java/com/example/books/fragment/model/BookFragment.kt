package com.example.books.fragment.model

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.books.R
import com.example.books.api.ApiFactory
import com.example.books.controller.AuthorLab
import com.example.books.controller.BookLab
import com.example.books.controller.PublisherLab
import com.example.books.fragment.list.BookListFragment
import com.example.books.model.Author
import com.example.books.model.Book
import com.example.books.model.Publisher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.UUID


class BookFragment : Fragment() {

    private var book: Book? = null
    private lateinit var titleField: EditText
    private lateinit var authorField: TextView
    private lateinit var publisherField: TextView
    private lateinit var yearField: EditText
    private lateinit var codeField: EditText
    private lateinit var countPageField: EditText
    private lateinit var hardcoverField: EditText
    private lateinit var essayField: EditText
    private lateinit var statusCheckBox: CheckBox
    private lateinit var deleteButton: Button
    private var createBook: Book = Book()
    private val service = ApiFactory.bookApi
    private val servicePublisher = ApiFactory.publisherApi
    private val serviceAuthor = ApiFactory.authorApi

    private lateinit var arrayListPublishers: ArrayList<String>
    private lateinit var arrayListAuthors: ArrayList<String>
    private lateinit var dialog: Dialog

    companion object {
        private const val ARG_BOOK_ID = "book_id"
        private const val TAG = "BookFragment"

        fun newInstance(bookId: UUID?) = BookFragment().apply {
            arguments = Bundle().apply {
                putSerializable(ARG_BOOK_ID, bookId)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super. onCreate(savedInstanceState)
        val bookId = requireArguments().getSerializable(ARG_BOOK_ID) as UUID?
        book = BookLab.get(requireActivity()).getBook(bookId as UUID)
        changeBook()
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val v = inflater.inflate(R.layout.fragment_book, container, false)

        titleField = v.findViewById(R.id.book_title)
        titleField.setText(book?.title)
        titleField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int) {
                book?.title = s.toString()
            }

            override fun afterTextChanged(c: Editable) {
            }
        })


        ///>>>>>>>>>>>>>>> Начало Author>>>>>>>>>>>>>>>>>>>>>>>>
        val authorLab = AuthorLab.get(requireActivity())
        val authors = authorLab.authors
        arrayListAuthors = ArrayList()

        if(authors.size == 0){
            loadDataAuthor()
        }

        for(i in authors){
            arrayListAuthors.add(getAuthorFullName(i))
        }

        authorField = v.findViewById(R.id.book_author)
        if(book?.author != null){
            authorField.text = getAuthorFullName(book?.author!!)
        }
        else{
            authorField.text = ""
        }

        authorField.setOnClickListener {
            dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_searchable_author)
            dialog.window?.setLayout(850, 900)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            dialog.show()

            val editText = dialog.findViewById<EditText>(R.id.dialog_search_author_edit_text)
            val listView = dialog.findViewById<ListView>(R.id.dialog_search_author_list_view)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, arrayListAuthors)
            listView.adapter = adapter

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    adapter.filter.filter(s)
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            listView.setOnItemClickListener { _, _, position, _ ->
                authorField.text = adapter.getItem(position)
                for(i in authors){
                    if(getAuthorFullName(i) == adapter.getItem(position)){
                        book?.author = i
                        break
                    }
                }
                dialog.dismiss()
            }
        }
        ///>>>>>>>>>>>>>>> Конец Author>>>>>>>>>>>>>>>>>>>>>>>>>>>


        ///>>>>>>>>>>>>>>> Начало Publisher>>>>>>>>>>>>>>>>>>>>>>>
        val publisherLab = PublisherLab.get(requireActivity())
        val publishers = publisherLab.publishers
        arrayListPublishers = ArrayList()

        if(publishers.size == 0){
            loadDataPublisher()
        }

        for(i in publishers){
            arrayListPublishers.add(i.name)
        }

        publisherField = v.findViewById(R.id.book_publisher)
        publisherField.text = book?.publisher?.name
        publisherField.setOnClickListener {
            dialog = Dialog(requireContext())
            dialog.setContentView(R.layout.dialog_searchable_publisher)
            dialog.window?.setLayout(850, 900)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.WHITE))
            dialog.show()

            val editText = dialog.findViewById<EditText>(R.id.dialog_search_publisher_edit_text)
            val listView = dialog.findViewById<ListView>(R.id.dialog_search_publisher_list_view)
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, arrayListPublishers)
            listView.adapter = adapter

            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    adapter.filter.filter(s)
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })

            listView.setOnItemClickListener { _, _, position, _ ->
                publisherField.text = adapter.getItem(position)
                for(i in publishers){
                    if(i.name == adapter.getItem(position)){
                        book?.publisher = i
                        break
                    }
                }
                dialog.dismiss()
            }
        }
        ///>>>>>>>>>>>>>>> Конец Publisher>>>>>>>>>>>>>>>>>>>>>>>>>>>

        yearField = v.findViewById(R.id.book_year)
        yearField.setText(book?.yearPublish)
        yearField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    book?.yearPublish = s.toString()
                } catch (nfe: NumberFormatException) {
                    book?.yearPublish = null
                }
            }

            override fun afterTextChanged(c: Editable) {
            }
        })


        codeField = v.findViewById(R.id.book_code)
        codeField.setText(book?.code)
        codeField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int) {
                book?.code = s.toString()
            }

            override fun afterTextChanged(c: Editable) {
            }
        })


        countPageField = v.findViewById(R.id.book_count_page)
        if(book?.countPage == null){
            countPageField.setText("")
        }
        else{
            countPageField.setText(book?.countPage.toString())
        }
        countPageField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int) {
                try {
                    book?.countPage = s.toString().toLong()
                } catch (nfe: NumberFormatException) {
                    book?.countPage = null
                }
            }

            override fun afterTextChanged(c: Editable) {
            }
        })


        hardcoverField = v.findViewById(R.id.book_hardcover)
        hardcoverField.setText(book?.hardcover)
        hardcoverField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int) {
                book?.hardcover = s.toString()
            }

            override fun afterTextChanged(c: Editable) {
            }
        })


        essayField = v.findViewById(R.id.book_essay)
        essayField.setText(book?.essay)
        essayField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int) {
                book?.essay = s.toString()
            }

            override fun afterTextChanged(c: Editable) {
            }
        })


        statusCheckBox = v.findViewById(R.id.book_status)
        statusCheckBox.isChecked = book?.status!!
        statusColor()
        statusCheckBox.setOnCheckedChangeListener{
                _: CompoundButton, isChecked: Boolean ->
            book?.status = isChecked
            statusColor()
        }


        deleteButton = v.findViewById(R.id.book_delete_button)
        deleteButton.setOnClickListener {
            val bookId = requireArguments().getSerializable(ARG_BOOK_ID) as UUID?
            book = BookLab.get(requireActivity()).getBook(bookId as UUID)
            BookLab.get(requireActivity()).deleteBook(book!!)
            BookListFragment.DELETE_INFO = true
            deleteBookById(bookId)
            activity?.finish()
        }
        return v
    }


    override fun onPause() {
        if(!BookListFragment.DELETE_INFO){
            if(createBook.title != book?.title
                || createBook.code != book?.code
                || createBook.hardcover != book?.hardcover
                || createBook.essay != book?.essay
                || createBook.author != book?.author
                || createBook.publisher != book?.publisher
                || createBook.countPage != book?.countPage
                || createBook.status != book?.status
                || createBook.yearPublish != book?.yearPublish)
            {
                putBook(book!!)
                changeBook()
            }
        }
        super.onPause()
    }


    private fun changeBook(){
        createBook.title = book?.title.toString()
        createBook.code = book?.code.toString()
        createBook.hardcover = book?.hardcover.toString()
        createBook.essay = book?.essay.toString()
        createBook.author = book?.author
        createBook.publisher = book?.publisher
        createBook.countPage = book?.countPage
        createBook.status = book?.status.toString().toBoolean()
        createBook.yearPublish = book?.yearPublish
    }


    private fun deleteBookById(bookId: UUID) = runBlocking {
        val job = launch {
            val request = service.deleteBookById(bookId)
            try {
                val response = request.await()
                if (response.isSuccessful) {
                    Log.d(TAG, "Успешное удаление книги $bookId")
                }
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
            }
        }
        job.join()
    }


    private fun putBook(book: Book) = runBlocking {
        val job = launch {
            val request = service.putBook(book)
            try {
                val response = request.await()
                if (response.isSuccessful) {
                    Log.d(TAG, "Успешное изменение книги ${book.id}")
                    println("Успешное изменение книги ${book.id}")
                }
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
            }
        }
        job.join()
    }


    private fun statusColor(){
        if(book!!.status){
            statusCheckBox.setTextColor(Color.parseColor("#FF388E3C"))
            statusCheckBox.text = "Есть в наличии"
        }
        else{
            statusCheckBox.setTextColor(Color.parseColor("#D32F2F"))
            statusCheckBox.text = "Нет в наличии"
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


    private fun loadDataAuthor() = runBlocking {
        val job = launch {
            val authorLab = AuthorLab.get(requireActivity())
            val postRequest = serviceAuthor.getAuthors()
            try {
                val response = postRequest.await()
                if (response.isSuccessful) {
                    authorLab.authors = response.body() as MutableList<Author>
                }
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
            }
        }
        job.join()
    }


    private fun loadDataPublisher() = runBlocking {
        val job = launch {
            val publisherLab = PublisherLab.get(requireActivity())
            val postRequest = servicePublisher.getPublishers()
            try {
                val response = postRequest.await()
                if (response.isSuccessful) {
                    publisherLab.publishers = response.body() as MutableList<Publisher>
                }
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
            }
        }
        job.join()
    }

}












