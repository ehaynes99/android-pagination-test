package com.example.paginationtest.data

data class PageInfo(
    val hasNextPage: Boolean,
    val hasPreviousPage: Boolean,
    val startCursor: Cursor?,
    val endCursor: Cursor?
)