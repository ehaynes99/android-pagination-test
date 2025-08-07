package com.example.paginationtest.contacts.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.paginationtest.R
import com.example.paginationtest.contacts.data.Contact
import com.example.paginationtest.contacts.data.Cursor
import com.example.paginationtest.contacts.ui.adapter.ContactAdapter
import com.example.paginationtest.contacts.ui.adapter.ContactLoadStateAdapter
import com.example.paginationtest.contacts.viewmodel.ContactViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ContactsFragment : Fragment() {
    
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ContactAdapter
    private lateinit var sharedPrefs: SharedPreferences
    
    private val viewModel: ContactViewModel by viewModels()
    
    private companion object {
        const val PREF_NAME = "contact_prefs"
        const val SAVED_CURSOR_KEY = "saved_cursor"
        const val SAVED_CURSOR_POSITION_KEY = "saved_cursor_position"
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_contacts, container, false)
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews(view)
        observePagingData()
    }
    
    private fun setupViews(view: View) {
        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = ContactAdapter { contact ->
            navigateToContactDetail(contact)
        }
        
        // Add loading state adapter for spinners
        val loadStateAdapter = ContactLoadStateAdapter { adapter.retry() }
        recyclerView.adapter = adapter.withLoadStateFooter(loadStateAdapter)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        
        sharedPrefs = requireContext().getSharedPreferences(PREF_NAME, android.content.Context.MODE_PRIVATE)
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
        val intent = Intent(requireContext(), ContactDetailActivity::class.java).apply {
            putExtra(ContactDetailActivity.EXTRA_CONTACT_NAME, "${contact.firstName} ${contact.lastName}")
            putExtra(ContactDetailActivity.EXTRA_CONTACT_COMPANY, contact.company)
        }
        startActivity(intent)
    }
}