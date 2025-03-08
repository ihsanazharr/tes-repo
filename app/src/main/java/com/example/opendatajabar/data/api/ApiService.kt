package com.example.opendatajabar.data.api

import retrofit2.http.GET

interface ApiService {
    @GET("od_17046_rata_rata_lama_sekolah_berdasarkan_kabupatenkota")
    suspend fun getData(): ApiResponse
}
