package com.rodionovmax.materialnasa.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rodionovmax.materialnasa.R
import com.rodionovmax.materialnasa.data.model.NewsArticle
import com.rodionovmax.materialnasa.data.model.SearchResult
import com.rodionovmax.materialnasa.databinding.SearchItemBinding

class SearchAdapter : PagingDataAdapter<SearchResult, SearchResultViewHolder>(SearchResultDiffItemCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder = SearchResultViewHolder(parent)

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class SearchResultViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
) {
    private val binding = SearchItemBinding.bind(itemView)

    fun bind(result: SearchResult?) {
        result?.let {
            binding.title.text = result.title
            binding.author.text = result.keywords.toString()
            Glide.with(itemView.context).load(result.imgUrl).into(binding.image)
        }
    }

    /*fun bind(article: NewsArticle?) {
        article?.let {
            binding.title.text = article.title
            binding.author.text = article.author
            Glide.with(itemView.context).load(article.urlToImage).into(binding.image)
        }
    }*/
}

private object SearchResultDiffItemCallback : DiffUtil.ItemCallback<SearchResult>() {
    override fun areItemsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
//        return oldItem.id == newItem.id && oldItem.title == newItem.title && oldItem.url == newItem.url
        return oldItem.id == newItem.id && oldItem.title == newItem.title && oldItem.imgUrl == newItem.imgUrl
    }

}

