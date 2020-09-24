package com.ishanvohra.dateline.View.Activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.ishanvohra.dateline.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity(){
    private lateinit var loginButton:Button
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var signUpTv: TextView
    private lateinit var auth: FirebaseAuth

    private val TAG: String = "LoginActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton = login_btn
        emailEditText = email_et
        passwordEditText = password_et
        signUpTv = login_sign_up_tv

        auth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            val email: String = emailEditText.text.toString()
            val password: String = passwordEditText.text.toString()

            if(email.isEmpty()){
                emailEditText.error = "Enter email"
                return@setOnClickListener
            }

            if(password.isEmpty()){
                passwordEditText.error = "Enter password"
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if(it.isSuccessful){
                    startActivity(MainActivity.newIntent(this@LoginActivity))
                    finish()
                }
            }

        }

        signUpTv.setOnClickListener {
            startActivity(SignupActivity.newIntent(this@LoginActivity))
        }
    }

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser

        if(user != null){
            startActivity(MainActivity.newIntent(this@LoginActivity))
        }

    }
    companion object{
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }
}
