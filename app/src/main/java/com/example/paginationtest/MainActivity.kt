package com.example.paginationtest

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paginationtest.adapter.ContactAdapter
import com.example.paginationtest.data.Contact
import com.example.paginationtest.data.Cursor
import com.example.paginationtest.viewmodel.ContactViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContactAdapter
    private lateinit var sharedPrefs: SharedPreferences
    
    private val viewModel: ContactViewModel by viewModels()
    
    private companion object {
        const val PREF_NAME = "contact_prefs"
        const val SAVED_CURSOR_KEY = "saved_cursor"
        const val SAVED_CURSOR_POSITION_KEY = "saved_cursor_position"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        
        setupViews()
        observePagingData()
    }
    
    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerView)
        adapter = ContactAdapter { contact ->
            navigateToContactDetail(contact)
        }
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        sharedPrefs = getSharedPreferences(PREF_NAME, MODE_PRIVATE)
    }
    
    private fun observePagingData() {
        lifecycleScope.launch {
            viewModel.contactsPagingData.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }
    
    override fun onPause() {
        super.onPause()
        saveCurrentScrollPosition()
    }
    
    private fun saveCurrentScrollPosition() {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager
        val firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
        
        if (firstVisiblePosition != RecyclerView.NO_POSITION) {
            val firstVisibleView = layoutManager.findViewByPosition(firstVisiblePosition)
            val offset = firstVisibleView?.top ?: 0
            
            val contact = adapter.peek(firstVisiblePosition)
            contact?.let {
                sharedPrefs.edit()
                    .putString(SAVED_CURSOR_KEY, it.sortKey)
                    .putInt(SAVED_CURSOR_POSITION_KEY, firstVisiblePosition)
                    .apply()
                viewModel.saveCurrentCursor(Cursor(it.sortKey))
            }
        }
    }
    
    private fun navigateToContactDetail(contact: Contact) {
        val intent = Intent(this, ContactDetailActivity::class.java).apply {
            putExtra(ContactDetailActivity.EXTRA_CONTACT_NAME, "${contact.firstName} ${contact.lastName}")
            putExtra(ContactDetailActivity.EXTRA_CONTACT_COMPANY, contact.company)
        }
        startActivity(intent)
    }
}