package com.example.voiceapplication

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.voiceapplication.Sentance

@Dao
interface ProductDao {

    @Query("SELECT * from sentance_table")
    fun getAllProduct(): LiveData<List<Sentance>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(product: Sentance)

}