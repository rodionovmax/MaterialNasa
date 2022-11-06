package com.rodionovmax.materialnasa.ui.explore.mars

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.rodionovmax.materialnasa.data.model.MarsPhoto

/*class MarsAdapter(
    private val onPhotoClickListener: (MarsPhoto) -> Unit
) : PagingDataAdapter<MarsPhoto, MarsViewHolder>(ARTICLE_DIFF_CALLBACK) {

    private var marsPhotos = mutableListOf<MarsPhoto>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarsViewHolder =
        MarsViewHolder(parent, onPhotoClickListener)

    override fun onBindViewHolder(holder: MarsViewHolder, position: Int) {
        holder.bind(marsPhotos[position])
    }

    override fun getItemCount(): Int = marsPhotos.size

    fun setData(list: List<MarsPhoto>) {
        marsPhotos.clear()
        marsPhotos.addAll(list)
        notifyDataSetChanged()
    }

    companion object {
        private val ARTICLE_DIFF_CALLBACK = object : DiffUtil.ItemCallback<MarsPhoto>() {
            override fun areItemsTheSame(oldItem: MarsPhoto, newItem: MarsPhoto): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: MarsPhoto, newItem: MarsPhoto): Boolean =
                oldItem == newItem
        }
    }
}*/

class MarsAdapter(
    private val onPhotoClickListener: (MarsPhoto) -> Unit
) : RecyclerView.Adapter<MarsViewHolder>() {

    private var marsPhotos = mutableListOf<MarsPhoto>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarsViewHolder =
        MarsViewHolder(parent, onPhotoClickListener)

    override fun onBindViewHolder(holder: MarsViewHolder, position: Int) {
        holder.bind(marsPhotos[position])
    }

    override fun getItemCount(): Int = marsPhotos.size

    fun setData(list: List<MarsPhoto>) {
        marsPhotos.clear()
        marsPhotos.addAll(list)
        notifyDataSetChanged()
    }

}


