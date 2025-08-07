package com.example.paginationtest.contacts.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.paginationtest.contacts.api.ContactApiClient
import com.example.paginationtest.contacts.data.Contact
import com.example.paginationtest.contacts.data.Cursor

class ContactPagingSource(
    private val apiClient: ContactApiClient,
    private val pageSize: Int = 100
) : PagingSource<Cursor, Contact>() {

    override suspend fun load(params: LoadParams<Cursor>): LoadResult<Cursor, Contact> {
        return try {
            val connection = when (params) {
                is LoadParams.Refresh -> {
                    // For refresh, use the provided key or start from beginning
                    val cursor = params.key
                    if (cursor != null) {
                        // When refreshing with a key, we want to load around that cursor
                        // Load some items before and after to provide context
                        apiClient.getContacts(
                            after = cursor,
                            first = pageSize
                        )
                    } else {
                        // Initial load
                        apiClient.getContacts(first = pageSize)
                    }
                }
                is LoadParams.Prepend -> {
                    // Load previous page
                    val cursor = params.key
                    apiClient.getContacts(
                        before = cursor,
                        last = pageSize
                    )
                }
                is LoadParams.Append -> {
                    // Load next page
                    val cursor = params.key
                    apiClient.getContacts(
                        after = cursor,
                        first = pageSize
                    )
                }
            }

            val contacts = connection.nodes
            
            LoadResult.Page(
                data = contacts,
                prevKey = if (connection.pageInfo.hasPreviousPage) connection.pageInfo.startCursor else null,
                nextKey = if (connection.pageInfo.hasNextPage) connection.pageInfo.endCursor else null
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    override fun getRefreshKey(state: PagingState<Cursor, Contact>): Cursor? {
        // Try to find the cursor of the item closest to the most recently accessed index
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey ?: anchorPage?.nextKey
        }
    }
}