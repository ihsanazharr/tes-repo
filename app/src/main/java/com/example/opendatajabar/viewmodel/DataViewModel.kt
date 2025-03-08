package com.example.opendatajabar.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.opendatajabar.data.local.AppDatabase
import com.example.opendatajabar.data.local.DataEntity
import com.example.opendatajabar.data.api.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DataViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getDatabase(application).dataDao()
    private val apiService = RetrofitClient.instance

    private val _rowCount = MutableLiveData<Int>()
    val rowCount: LiveData<Int> get() = _rowCount

    private val _dataList = MutableLiveData<List<DataEntity>>()
    val dataList: LiveData<List<DataEntity>> = _dataList

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _apiError = MutableLiveData<String?>()
    val apiError: LiveData<String?> = _apiError

    init {
        fetchLocalData()
        fetchRowCount()
        fetchDataFromApi()
    }

    fun fetchRowCount() {
        viewModelScope.launch(Dispatchers.IO) {
            val count = dao.getCount()
            withContext(Dispatchers.Main) {
                _rowCount.value = count
            }
        }
    }

    private fun fetchLocalData() {
        viewModelScope.launch(Dispatchers.IO) {
            val localData = dao.getAll()
            withContext(Dispatchers.Main) {
                _dataList.value = localData
            }
        }
    }

    fun fetchDataFromApi() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)
            try {
                val response = apiService.getData()
                if (response.error == 0) {
                    withContext(Dispatchers.Main) {
                        _dataList.value = response.data
                    }
                    dao.insertAll(response.data) // Simpan ke Room Database
                } else {
                    _apiError.postValue("Error: ${response.message}")
                }
            } catch (e: Exception) {
                _apiError.postValue("Failed to fetch data: ${e.message}")
            } finally {
                _isLoading.postValue(false)
            }
        }
    }

    fun insertData(data: DataEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.insert(data)

            val updatedList = dao.getAll()
            withContext(Dispatchers.Main) {
                _dataList.value = updatedList
                _rowCount.value = updatedList.size
            }
        }
    }

    fun updateData(data: DataEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.update(data)

            val updatedList = dao.getAll()
            withContext(Dispatchers.Main) {
                _dataList.value = updatedList
            }
        }
    }

    fun deleteData(data: DataEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            dao.delete(data)

            val updatedList = dao.getAll()
            withContext(Dispatchers.Main) {
                _dataList.value = updatedList
                _rowCount.value = updatedList.size
            }
        }
    }

    suspend fun getDataById(id: Int): DataEntity? {
        return withContext(Dispatchers.IO) {
            dao.getById(id)
        }
    }
}