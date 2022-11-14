package com.rodionovmax.materialnasa.ui.search

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rodionovmax.materialnasa.R
import com.rodionovmax.materialnasa.data.model.SearchResult
import com.rodionovmax.materialnasa.databinding.SearchItemBinding

class SearchResultViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    private val image: ImageView = view.findViewById(R.id.image)
    private val title: TextView = view.findViewById(R.id.title)
    private val keywords: TextView = view.findViewById(R.id.author)

    private var searchResult: SearchResult? = null

//    init {
//        view.setOnClickListener {
//            searchResult?.url?.let { url ->
//                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//                view.context.startActivity(intent)
//            }
//        }
//    }

    fun bind(res: SearchResult?) {
        if (res == null) {
            val resources = itemView.resources
            title.text = resources.getString(R.string.loading)
            image.visibility = View.GONE
            keywords.text = resources.getString(R.string.unknown)
        } else {
            showSearchResultData(res)
        }
    }

    private fun showSearchResultData(res: SearchResult) {
        title.text = res.title
        keywords.text = res.keywords.toString()
        Glide.with(itemView.context).load(res.imgUrl).into(image)
//        this.searchResult = res
//        title.text = res.title

//        // if the description is missing, hide the TextView
//        var descriptionVisibility = View.GONE
//        if (repo.description != null) {
//            description.text = repo.description
//            descriptionVisibility = View.VISIBLE
//        }
//        description.visibility = descriptionVisibility
//
//        stars.text = repo.stars.toString()
//        forks.text = repo.forks.toString()
//
//        // if the language is missing, hide the label and the value
//        var languageVisibility = View.GONE
//        if (!repo.language.isNullOrEmpty()) {
//            val resources = this.itemView.context.resources
//            language.text = resources.getString(R.string.language, repo.language)
//            languageVisibility = View.VISIBLE
//        }
//        language.visibility = languageVisibility
    }

    companion object {
        fun create(parent: ViewGroup): SearchResultViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.search_item, parent, false)
            return SearchResultViewHolder(view)
        }
    }

}