package com.example.opendatajabar.data.api

import com.example.opendatajabar.data.local.DataEntity
import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("message") val message: String,
    @SerializedName("error") val error: Int,
    @SerializedName("data") val data: List<DataEntity>
)
