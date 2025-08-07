package com.example.paginationtest.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.paginationtest.api.ContactApiClient
import com.example.paginationtest.data.Contact
import com.example.paginationtest.data.Cursor
import com.example.paginationtest.repository.ContactRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ContactViewModel(application: Application) : AndroidViewModel(application) {
    
    private val apiClient = ContactApiClient(application.applicationContext)
    private val repository = ContactRepository(apiClient)
    
    private val _savedCursor = MutableStateFlow<Cursor?>(null)
    val savedCursor: StateFlow<Cursor?> = _savedCursor.asStateFlow()
    
    val contactsPagingData: Flow<PagingData<Contact>> = repository
        .getContactsPagingData(_savedCursor.value)
        .cachedIn(viewModelScope)
    
    fun saveCurrentCursor(cursor: Cursor) {
        _savedCursor.value = cursor
    }
    
    fun refreshWithCursor(cursor: Cursor? = null): Flow<PagingData<Contact>> {
        return repository
            .getContactsPagingData(cursor)
            .cachedIn(viewModelScope)
    }
}