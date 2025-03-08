package com.example.opendatajabar.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(profile: ProfileEntity)

    @Update
    suspend fun update(profile: ProfileEntity)

    @Query("SELECT * FROM profile_table WHERE id = 1 LIMIT 1")
    fun getProfile(): Flow<ProfileEntity?>

    @Query("UPDATE profile_table SET image = :image WHERE id = 1")
    suspend fun updateProfileImage(image: ByteArray)

    @Query("DELETE FROM profile_table")
    suspend fun deleteAll()
}