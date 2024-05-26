package com.capstone.greenavo.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.capstone.greenavo.data.api.response.LoginResult
import com.capstone.greenavo.data.repository.UserRepository

class MainViewModel(private val repository: UserRepository) : ViewModel() {
    fun getSession(): LiveData<LoginResult>{
        return repository.getSession().asLiveData()
    }
}