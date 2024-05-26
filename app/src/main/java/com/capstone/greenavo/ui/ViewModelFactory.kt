package com.capstone.greenavo.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.greenavo.data.api.response.RegisterResponse
import com.capstone.greenavo.data.repository.UserRepository
import com.capstone.greenavo.di.Injection
import com.capstone.greenavo.ui.camera.CameraViewModel
import com.capstone.greenavo.ui.login.LoginViewModel
import com.capstone.greenavo.ui.main.MainViewModel
import com.capstone.greenavo.ui.profile.ProfileViewModel
import com.capstone.greenavo.ui.register.RegisterViewModel

class ViewModelFactory (
    private val userRepository: UserRepository
) : ViewModelProvider.NewInstanceFactory(){

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(RegisterViewModel::class.java) ->{
                RegisterViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(CameraViewModel::class.java) -> {
                CameraViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object{
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            return ViewModelFactory(
                Injection.provideRepository(context)
            )
        }
    }
}