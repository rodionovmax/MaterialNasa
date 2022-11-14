package com.rodionovmax.materialnasa.ui.search

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rodionovmax.materialnasa.R

class SearchAdapter : PagingDataAdapter<UiModel, RecyclerView.ViewHolder>(UIMODEL_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == R.layout.search_item) {
            SearchResultViewHolder.create(parent)
        } else {
            SeparatorViewHolder.create(parent)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is UiModel.ResItem -> R.layout.search_item
            is UiModel.SeparatorItem -> R.layout.separator_view_item
            null -> throw UnsupportedOperationException("Unknown view")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val uiModel = getItem(position)
        uiModel.let {
            when (uiModel) {
                is UiModel.ResItem -> (holder as SearchResultViewHolder).bind(uiModel.res)
                is UiModel.SeparatorItem -> (holder as SeparatorViewHolder).bind(uiModel.description)
                else -> {}
            }
        }
    }

    companion object {
        private val UIMODEL_COMPARATOR = object : DiffUtil.ItemCallback<UiModel>() {
            override fun areItemsTheSame(oldItem: UiModel, newItem: UiModel): Boolean {
                return (oldItem is UiModel.ResItem && newItem is UiModel.ResItem &&
                        oldItem.res.title == newItem.res.title) ||
                        (oldItem is UiModel.SeparatorItem && newItem is UiModel.SeparatorItem &&
                                oldItem.description == newItem.description)
            }

            override fun areContentsTheSame(oldItem: UiModel, newItem: UiModel): Boolean =
                oldItem == newItem
        }
    }
}

//class SearchAdapter : PagingDataAdapter<SearchResult, SearchResultViewHolder>(SearchResultDiffItemCallback) {
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder = SearchResultViewHolder(parent)
//
//    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
//        holder.bind(getItem(position))
//    }
//}
//
//class SearchResultViewHolder(parent: ViewGroup): RecyclerView.ViewHolder(
//    LayoutInflater.from(parent.context).inflate(R.layout.search_item, parent, false)
//) {
//    private val binding = SearchItemBinding.bind(itemView)
//
//    fun bind(result: SearchResult?) {
//        result?.let {
//            binding.title.text = result.title
//            binding.author.text = result.keywords.toString()
//            Glide.with(itemView.context).load(result.imgUrl).into(binding.image)
//        }
//    }
//
//    /*fun bind(article: NewsArticle?) {
//        article?.let {
//            binding.title.text = article.title
//            binding.author.text = article.author
//            Glide.with(itemView.context).load(article.urlToImage).into(binding.image)
//        }
//    }*/
//}
//
//private object SearchResultDiffItemCallback : DiffUtil.ItemCallback<SearchResult>() {
//    override fun areItemsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
//        return oldItem == newItem
//    }
//
//    override fun areContentsTheSame(oldItem: SearchResult, newItem: SearchResult): Boolean {
////        return oldItem.id == newItem.id && oldItem.title == newItem.title && oldItem.url == newItem.url
//        return oldItem.id == newItem.id && oldItem.title == newItem.title && oldItem.imgUrl == newItem.imgUrl
//    }
//
//}

