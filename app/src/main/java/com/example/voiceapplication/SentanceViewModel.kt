package com.example.voiceapplication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SentanceViewModel (application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: SentanceRepository
    // LiveData gives us updated Products when they change.
    val allProducts: LiveData<List<Sentance>>

    init {
        // Gets reference to ProductDao from ProductRoomDatabase to construct
        // the correct ProductRepository.
        val productDao = ProductRoomDatabase.getDatabase(application).productDao()
        repository = SentanceRepository(productDao)
        allProducts = repository.allWords
    }

    fun insert(product: Sentance) = viewModelScope.launch {
        repository.insert(product)
    }

}