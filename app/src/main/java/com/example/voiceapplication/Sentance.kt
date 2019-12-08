package com.example.voiceapplication

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "sentance_table")
data class Sentance(@ColumnInfo(name = "sentance") val name: String){
    @PrimaryKey(autoGenerate = true)
    var id: Int= 0
}