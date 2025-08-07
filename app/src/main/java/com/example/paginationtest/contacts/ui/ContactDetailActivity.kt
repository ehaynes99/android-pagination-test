package com.example.paginationtest.contacts.ui

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.paginationtest.R

class ContactDetailActivity : AppCompatActivity() {
    
    companion object {
        const val EXTRA_CONTACT_NAME = "contact_name"
        const val EXTRA_CONTACT_COMPANY = "contact_company"
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_detail)
        
        val contactName = intent.getStringExtra(EXTRA_CONTACT_NAME) ?: "Unknown"
        val contactCompany = intent.getStringExtra(EXTRA_CONTACT_COMPANY) ?: "Unknown"
        
        findViewById<TextView>(R.id.nameTextView).text = contactName
        findViewById<TextView>(R.id.companyTextView).text = contactCompany
        
        findViewById<Button>(R.id.backButton).setOnClickListener {
            finish()
        }
    }
}