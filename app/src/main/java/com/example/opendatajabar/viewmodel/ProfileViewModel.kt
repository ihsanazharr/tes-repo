package com.example.opendatajabar.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.opendatajabar.data.local.AppDatabase
import com.example.opendatajabar.data.local.ProfileEntity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.flow.firstOrNull

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).profileDao()

    private val _profile = MutableStateFlow<ProfileEntity?>(null)
    val profile: StateFlow<ProfileEntity?> = _profile

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _profile.value = dao.getProfile().firstOrNull()
        }
    }

    fun updateProfile(name: String, studentId: String, email: String) {
        viewModelScope.launch {
            val currentProfile = dao.getProfile().firstOrNull()
            if (currentProfile == null) {
                dao.insert(ProfileEntity(id = 1, name = name, studentId = studentId, email = email))
            } else {
                dao.update(currentProfile.copy(name = name, studentId = studentId, email = email))
            }
            _profile.value = dao.getProfile().firstOrNull()
        }
    }


    fun updateProfileImage(uri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val byteArray = inputStream?.readBytes()
                inputStream?.close()

                if (byteArray != null) {
                    val profileData = dao.getProfile().firstOrNull()
                    if (profileData == null) {
                        dao.insert(ProfileEntity(id = 1, name = "", studentId = "", email = "", image = byteArray))
                    } else {
                        dao.updateProfileImage(byteArray)
                    }
                    _profile.value = dao.getProfile().firstOrNull()
                }
            } catch (e: Exception) {
                Log.e("ProfileViewModel", "Error updating profile image", e)
            }
        }
    }

    fun isEmailUnique(email: String): Boolean {
        return email == profile.value?.email
    }

    private fun saveImageToInternalStorage(uri: Uri, context: Context): File {
        val inputStream = context.contentResolver.openInputStream(uri)!!
        val file = File(context.filesDir, "profile.jpg")
        val outputStream = FileOutputStream(file)
        inputStream.copyTo(outputStream)
        inputStream.close()
        outputStream.close()
        return file
    }
}
