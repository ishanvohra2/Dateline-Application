package com.ishanvohra.dateline

import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.ishanvohra.dateline.Model.User
import com.ishanvohra.dateline.utils.USER_DATA

class Repository {

    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    public fun updateUserDetails(user: User) : MutableLiveData<Task<Void>> {
        val result: MutableLiveData<Task<Void>> = MutableLiveData()
        databaseReference.child(USER_DATA).child(user.uid!!).setValue(user).addOnCompleteListener {
                result.value = it
        }

        return result
    }

    public fun signUpUser(email: String, password: String) : MutableLiveData<Task<AuthResult>>{
        val result: MutableLiveData<Task<AuthResult>> = MutableLiveData()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            result.value = it
        }

        return result
    }

}