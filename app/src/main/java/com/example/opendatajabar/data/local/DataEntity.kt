package com.example.opendatajabar.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "data_table")
data class DataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @SerializedName("kode_provinsi")
    val kodeProvinsi: Int,

    @SerializedName("nama_provinsi")
    val namaProvinsi: String,

    @SerializedName("kode_kabupaten_kota")
    val kodeKabupatenKota: Int,

    @SerializedName("nama_kabupaten_kota")
    val namaKabupatenKota: String,

    @SerializedName("rata_rata_lama_sekolah")
    val rataRataLamaSekolah: Double,

    @SerializedName("satuan")
    val satuan: String,

    @SerializedName("tahun")
    val tahun: Int
)