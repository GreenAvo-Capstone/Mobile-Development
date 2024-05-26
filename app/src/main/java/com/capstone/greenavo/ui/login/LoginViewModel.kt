package com.capstone.greenavo.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.capstone.greenavo.data.api.response.LoginResult
import com.capstone.greenavo.data.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: UserRepository) : ViewModel() {
    fun login(email: String, password: String) = repository.login(email, password)

    fun saveSession(user: LoginResult){
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}