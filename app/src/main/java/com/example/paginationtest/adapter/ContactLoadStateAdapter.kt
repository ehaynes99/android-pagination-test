package com.example.paginationtest.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView

class ContactLoadStateAdapter(
    private val retry: () -> Unit
) : LoadStateAdapter<ContactLoadStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return LoadStateViewHolder(view, retry)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    class LoadStateViewHolder(
        itemView: View,
        private val retry: () -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val titleText: TextView = itemView.findViewById(android.R.id.text1)
        private val subtitleText: TextView = itemView.findViewById(android.R.id.text2)
        
        init {
            itemView.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            when (loadState) {
                is LoadState.Loading -> {
                    titleText.text = "Loading..."
                    subtitleText.text = "Fetching more contacts"
                }
                is LoadState.Error -> {
                    titleText.text = "Error loading contacts"
                    subtitleText.text = "Tap to retry"
                }
                is LoadState.NotLoading -> {
                    if (loadState.endOfPaginationReached) {
                        titleText.text = "End of list"
                        subtitleText.text = "No more contacts to load"
                    } else {
                        titleText.text = ""
                        subtitleText.text = ""
                    }
                }
            }
        }
    }
}