package com.example.books.fragment

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.books.R
import com.example.books.activity.list.BookListActivity
import com.example.books.api.ApiFactory
import com.example.books.model.User
import com.example.books.model.UserLogin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class RegistrationFragment : Fragment() {

    private var user: User = User("","","")
    private lateinit var loginField: EditText
    private lateinit var passwordField: EditText
    private lateinit var emailField: EditText
    private lateinit var registrationButton: Button
    private val service = ApiFactory.userApi

    companion object{
        private const val TAG = "RegistrationFragment"
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_registration, container, false)

        loginField = view.findViewById(R.id.registration_login)
        loginField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int) {
                user.username = s.toString()
            }

            override fun afterTextChanged(c: Editable) {
            }
        })


        passwordField = view.findViewById(R.id.registration_password)
        passwordField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int) {
                user.password = s.toString()
            }

            override fun afterTextChanged(c: Editable) {
            }
        })


        emailField = view.findViewById(R.id.registration_email)
        emailField.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int, before: Int, count: Int) {
                user.email = s.toString()
            }

            override fun afterTextChanged(c: Editable) {
            }
        })


        registrationButton = view.findViewById(R.id.registration_new_account_button)
        registrationButton.setOnClickListener {
            if(user.username == ""){
                val text = "Введите логин"
                Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
            }
            else if(user.password == ""){
                val text = "Введите пароль"
                Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
            }
            else if(user.email == ""){
                val text = "Введите почту"
                Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
            }
            else{
                postRegistration(user)
            }
        }

        return view
    }


    private fun postRegistration(user: User) = runBlocking {
        val job = launch {
            val request = service.registration(user)
            try {
                val response = request.await()
                if (response.isSuccessful) {
                    Log.d(TAG, "Успешная регистрация")
                    AuthorizationFragment.USER_TOKEN = response.body()
                    val intent = Intent(requireContext(), BookListActivity::class.java)
                    startActivity(intent)
                }
            } catch (e: Exception) {
                val text = "Такой логин уже занят"
                Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
                Log.d(TAG, e.message.toString())
            }
        }
        job.join()
    }

}