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
import com.ishanvohra.dateline.data.User
import com.ishanvohra.dateline.utils.USER_DATA
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ishanvohra.dateline.R
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {

    private lateinit var signupButton: Button
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginTv: TextView

    private lateinit var mAuth:FirebaseAuth
    private lateinit var mRef:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        signupButton = signup_btn
        emailEditText = email_et
        passwordEditText = password_et
        loginTv = login_sign_up_tv

        mAuth = FirebaseAuth.getInstance()
        mRef = FirebaseDatabase.getInstance().reference

        loginTv.setOnClickListener {
            startActivity(LoginActivity.newIntent(this@SignupActivity))
        }

        signupButton.setOnClickListener {
            signUp(emailEditText.text.toString(),passwordEditText.text.toString())
        }
    }

    fun signUp(email:String, password:String){

        if(!email.isEmpty() && !password.isEmpty()){
            mAuth.createUserWithEmailAndPassword(emailEditText.text.toString(),passwordEditText.text.toString())
                .addOnCompleteListener {task->
                    if(task.isSuccessful){
                        val email = emailEditText.text.toString()
                        val uid = mAuth.currentUser?.uid ?: ""
                        val user = User(uid=uid,email=email)
                        mRef.child(USER_DATA).child(uid).setValue(user)
                        startActivity(CompleteProfileActivity.newIntent(this@SignupActivity))
                        finish()
                    }
                    else{
                        Toast.makeText(this@SignupActivity,"${task.exception?.localizedMessage}",Toast.LENGTH_SHORT).show()
                    }
                }
        }else{
            Toast.makeText(this@SignupActivity,"Empty Fields",Toast.LENGTH_SHORT).show()
        }
    }

    companion object{
        fun newIntent(context: Context) = Intent(context, SignupActivity::class.java)
    }
}
