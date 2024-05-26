package com.capstone.greenavo.di

import android.content.Context
import com.capstone.greenavo.data.api.ApiConfig
import com.capstone.greenavo.data.pref.UserPreference
import com.capstone.greenavo.data.pref.dataStore
import com.capstone.greenavo.data.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getUserSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(apiService, pref)
    }
}