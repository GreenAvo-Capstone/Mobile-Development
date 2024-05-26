package com.capstone.greenavo.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.capstone.greenavo.data.ResultState
import com.capstone.greenavo.data.api.ApiService
import com.capstone.greenavo.data.api.response.LoginResponse
import com.capstone.greenavo.data.api.response.LoginResult
import com.capstone.greenavo.data.api.response.RegisterResponse
import com.capstone.greenavo.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow
import org.json.JSONException
import org.json.JSONObject
import retrofit2.HttpException
import java.io.IOException

class UserRepository private constructor(
    private val userPreference: UserPreference, private val apiService: ApiService
){
    suspend fun saveSession(user: LoginResult) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<LoginResult> {
        return userPreference.getUserSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    fun register(name: String, email: String, password: String): LiveData<ResultState<RegisterResponse>> = liveData{
        emit(ResultState.Loading)
        try {
            val result = apiService.register(name, email, password)
            emit(ResultState.Success(result))
        }catch (e: Exception){
            when(e){
                is HttpException -> {
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorMessage = extractErrorMessage(errorBody)
                    emit(ResultState.Error(errorMessage ?: "An unknown error occurred"))
                }
                is IOException -> {
                    emit(ResultState.Error("Network error. Please check your connection and try again."))
                }
                else -> {
                    emit(ResultState.Error("An unknown error occurred"))
                }
            }
        }
    }

    fun login(email: String, password: String): LiveData<ResultState<LoginResponse>> = liveData {
        emit(ResultState.Loading)
        try {
            val result = apiService.login(email, password)
            emit(ResultState.Success(result))
        }catch (e: Exception){
            when(e){
                is HttpException -> {
                    // Ada response dari server, coba ekstrak pesan kesalahan dari body
                    val errorBody = e.response()?.errorBody()?.string()
                    val errorMessage = extractErrorMessage(errorBody)
                    emit(ResultState.Error(errorMessage ?: "An unknown error occurred"))
                }
                is IOException -> {
                    emit(ResultState.Error("Network error. Please check your connection and try again."))
                }
                else -> {
                    emit(ResultState.Error("An unknown error occurred"))
                }
            }
        }
    }

    private fun extractErrorMessage(errorBody: String?): String? {
        return try {
            val json = JSONObject(errorBody)
            json.getString("message")
        } catch (e: JSONException) {
            null
        }
    }

    companion object {
        fun getInstance(
            apiService: ApiService,
            userPreference: UserPreference
        ): UserRepository = UserRepository(userPreference, apiService)
    }
}