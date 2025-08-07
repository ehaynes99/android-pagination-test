package com.example.paginationtest.api

import android.content.Context
import android.util.Log
import com.example.paginationtest.data.*
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import java.io.InputStreamReader
import kotlin.random.Random

class ContactApiClient(private val context: Context) {
    
    companion object {
        private const val TAG = "ContactApiClient"
    }
    
    private val gson = Gson()
    private val prettyGson = GsonBuilder().setPrettyPrinting().create()
    private var _contacts: List<Contact>? = null
    private var fetchCount = 0
    
    private val contacts: List<Contact>
        get() {
            if (_contacts == null) {
                loadContacts()
            }
            return _contacts!!
        }
    
    private fun loadContacts() {
        val inputStream = context.assets.open("contacts.json")
        val reader = InputStreamReader(inputStream)
        val contactType = object : TypeToken<List<Contact>>() {}.type
        _contacts = gson.fromJson(reader, contactType)
        reader.close()
    }
    
    suspend fun getContacts(
        after: Cursor? = null,
        before: Cursor? = null,
        first: Int? = null,
        last: Int? = null
    ): ContactConnection {
        val inputParts = mutableListOf<String>()
        after?.let { inputParts.add("after: ${it.sortKey}") }
        before?.let { inputParts.add("before: ${it.sortKey}") }
        first?.let { inputParts.add("first: $it") }
        last?.let { inputParts.add("last: $it") }
        Log.d(TAG, "INPUT: ${inputParts.joinToString(", ")}")
        
        // Simulate network delay
        val delayMs = Random.nextLong(100, 301)
        delay(delayMs)
        
        fetchCount++
        val allContacts = contacts
        
        // Find the index range based on cursors
        val startIndex = when {
            after != null -> {
                val afterIndex = allContacts.indexOfFirst { it.sortKey == after.sortKey }
                if (afterIndex == -1) 0 else afterIndex + 1
            }
            before != null -> {
                val beforeIndex = allContacts.indexOfFirst { it.sortKey == before.sortKey }
                if (beforeIndex == -1) allContacts.size else 0
            }
            else -> 0
        }
        
        val endIndex = when {
            before != null -> {
                val beforeIndex = allContacts.indexOfFirst { it.sortKey == before.sortKey }
                if (beforeIndex == -1) allContacts.size else beforeIndex
            }
            after != null -> allContacts.size
            else -> allContacts.size
        }
        
        // Get the slice based on pagination parameters
        val slicedContacts = when {
            first != null -> {
                val actualEndIndex = minOf(startIndex + first, endIndex)
                allContacts.subList(startIndex, actualEndIndex)
            }
            last != null -> {
                val actualStartIndex = maxOf(endIndex - last, startIndex)
                allContacts.subList(actualStartIndex, endIndex)
            }
            else -> allContacts.subList(startIndex, endIndex)
        }
        
        // Create edges
        val edges = slicedContacts.map { contact ->
            ContactEdge(
                node = contact,
                cursor = Cursor(contact.sortKey)
            )
        }
        
        // Create page info
        val hasNextPage = when {
            first != null -> (startIndex + first) < allContacts.size
            else -> false
        }
        
        val hasPreviousPage = when {
            last != null -> (endIndex - last) > 0
            after != null -> startIndex > 0
            else -> false
        }
        
        val pageInfo = PageInfo(
            hasNextPage = hasNextPage,
            hasPreviousPage = hasPreviousPage,
            startCursor = edges.firstOrNull()?.cursor,
            endCursor = edges.lastOrNull()?.cursor
        )
        
        val result = ContactConnection(
            edges = edges,
            nodes = slicedContacts,
            pageInfo = pageInfo
        )
        
        Log.d(TAG, "OUTPUT pageInfo: ${prettyGson.toJson(pageInfo)}")
        Log.d(TAG, "OUTPUT size: ${slicedContacts.size}")
        Log.d(TAG, "Total fetches: $fetchCount (delayed ${delayMs}ms)")
        
        return result
    }
}