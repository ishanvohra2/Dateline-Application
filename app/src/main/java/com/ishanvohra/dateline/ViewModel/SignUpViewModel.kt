package com.ishanvohra.dateline.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.ishanvohra.dateline.Model.User
import com.ishanvohra.dateline.Repository

class SignUpViewModel : ViewModel() {

    private val repository: Repository = Repository()

    public fun updateUser(user: User) : MutableLiveData<Task<Void>> {
        return repository.updateUserDetails(user)
    }

    public fun signUpUser(email: String, password: String) : MutableLiveData<Task<AuthResult>> {
        return repository.signUpUser(email, password)
    }

}