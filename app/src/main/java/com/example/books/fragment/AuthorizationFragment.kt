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
import com.example.books.activity.RegistrationActivity
import com.example.books.activity.list.BookListActivity
import com.example.books.api.ApiFactory
import com.example.books.model.User
import com.example.books.model.UserLogin
import com.example.books.model.UserToken
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class AuthorizationFragment : Fragment() {

    private var user: UserLogin = UserLogin("","")
    private lateinit var loginField: EditText
    private lateinit var passwordField: EditText
    private lateinit var newAccountLink: TextView
    private lateinit var logInButton: Button
    private val service = ApiFactory.userApi

    companion object{
        private const val TAG = "AuthorizationFragment"
        var USER_TOKEN: UserToken? = null
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_authorization, container, false)

        loginField = view.findViewById(R.id.authorization_login)
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


        passwordField = view.findViewById(R.id.authorization_password)
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


        logInButton = view.findViewById(R.id.authorization_log_in_button)
        logInButton.setOnClickListener {
            postLogin(user)
        }


        newAccountLink = view.findViewById(R.id.authorization_new_account)
        newAccountLink.setOnClickListener {
            val context = view.context
            val intent = RegistrationActivity.newIntent(context)
            context.startActivity(intent)
        }

        return view
    }


    private fun postLogin(userLogin: UserLogin) = runBlocking {
        val job = launch {
            val request = service.login(userLogin)
            try {
                val response = request.await()
                if (response.isSuccessful) {
                    Log.d(TAG, "Успешная авторизация")
                    USER_TOKEN = response.body()
                    val intent = Intent(requireContext(), BookListActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Log.d(TAG, "Нет доступа авторизации")
                    val text = "Неверный логин или пароль"
                    Toast.makeText(requireContext(), text, Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Log.d(TAG, e.message.toString())
            }
        }
        job.join()
    }

}