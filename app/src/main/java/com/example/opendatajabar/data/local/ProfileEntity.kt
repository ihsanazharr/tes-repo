package com.example.opendatajabar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile_table")
data class ProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val studentId: String,
    val email: String,
    val image: ByteArray? = null // Simpan gambar dalam bentuk ByteArray
)
