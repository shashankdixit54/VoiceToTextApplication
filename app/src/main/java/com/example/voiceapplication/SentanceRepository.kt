package com.example.voiceapplication

import androidx.lifecycle.LiveData

class SentanceRepository(private val productDao: ProductDao) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allWords: LiveData<List<Sentance>> = productDao.getAllProduct()

    suspend fun insert(product: Sentance) {
        productDao.insert(product)
    }


}