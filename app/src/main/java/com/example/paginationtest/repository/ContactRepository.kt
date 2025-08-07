package com.example.paginationtest.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.paginationtest.api.ContactApiClient
import com.example.paginationtest.data.Contact
import com.example.paginationtest.data.Cursor
import com.example.paginationtest.paging.ContactPagingSource
import kotlinx.coroutines.flow.Flow

class ContactRepository(private val apiClient: ContactApiClient) {
    
    fun getContactsPagingData(initialCursor: Cursor? = null): Flow<PagingData<Contact>> {
        return Pager(
            config = PagingConfig(
                pageSize = 100,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            initialKey = initialCursor,
            pagingSourceFactory = { ContactPagingSource(apiClient) }
        ).flow
    }
}