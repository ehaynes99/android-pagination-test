package com.example.paginationtest.contacts.data

data class ContactConnection(
    val edges: List<ContactEdge>,
    val nodes: List<Contact>,
    val pageInfo: PageInfo
)