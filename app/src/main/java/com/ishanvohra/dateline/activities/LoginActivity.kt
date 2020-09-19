package com.ishanvohra.dateline.activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.ishanvohra.dateline.R
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var loginButton:Button
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var signUpTv: TextView
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        loginButton = login_btn
        emailEditText = email_et
        passwordEditText = password_et
        signUpTv = login_sign_up_tv

        mAuth = FirebaseAuth.getInstance()

        loginButton.setOnClickListener {
            login(emailEditText.text.toString(),passwordEditText.text.toString())
        }

        signUpTv.setOnClickListener {
            startActivity(SignupActivity.newIntent(this@LoginActivity))
        }
    }

    override fun onStart() {
        super.onStart()
        val user = mAuth.currentUser

        if(user != null){
            startActivity(MainActivity.newIntent(this@LoginActivity))
        }

    }

    fun login(email:String, password:String){

        if(!email.isEmpty() && !password.isEmpty()){
            mAuth.signInWithEmailAndPassword(emailEditText.text.toString(),passwordEditText.text.toString())
                .addOnCompleteListener {task->
                    if(task.isSuccessful){
                        startActivity(MainActivity.newIntent(this@LoginActivity))
                        finish()
                    }
                    else{
                        Toast.makeText(this@LoginActivity,"${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
        }else{
            Toast.makeText(this@LoginActivity,"Empty Fields", Toast.LENGTH_SHORT).show()
        }
    }

    companion object{
        fun newIntent(context: Context) = Intent(context, LoginActivity::class.java)
    }
}
