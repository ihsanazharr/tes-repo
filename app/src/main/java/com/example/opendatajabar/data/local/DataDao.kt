package com.example.opendatajabar.data.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(data: DataEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(dataList: List<DataEntity>)

    @Update
    suspend fun update(data: DataEntity)

    @Query("SELECT * FROM data_table ORDER BY id DESC")
    suspend fun getAll(): List<DataEntity>

    @Query("SELECT * FROM data_table WHERE id = :dataId")
    suspend fun getById(dataId: Int): DataEntity?

    @Query("SELECT COUNT(*) FROM data_table")
    suspend fun getCount(): Int

    @Delete
    suspend fun delete(data: DataEntity)
}