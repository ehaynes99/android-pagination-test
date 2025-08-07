package com.example.paginationtest.contacts.data

data class PageInfo(
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val startCursor: Cursor?,
    val endCursor: Cursor?
)