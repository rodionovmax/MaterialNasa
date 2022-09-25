package com.rodionovmax.materialnasa.ui.gallery

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodionovmax.materialnasa.data.model.Pod

class GalleryAdapter(
    private val listener: OnDeleteButtonClickedListener?
) : RecyclerView.Adapter<GalleryViewHolder>() {

    private var gallery = mutableListOf<Pod>()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder = GalleryViewHolder(parent)

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(gallery[position])
        holder.updateView()

        holder.onDeleteClick = {
            val removedItem = removeItem(it)
            listener?.removeFromDatabase(removedItem)
        }

    }

    override fun getItemCount(): Int = gallery.size

    fun setData(list: List<Pod>) {
        gallery.clear()
        gallery.addAll(list)
        notifyDataSetChanged()
    }

    private fun removeItem(viewHolder: RecyclerView.ViewHolder): Pod {

        val position = viewHolder.adapterPosition
        val removedItem = gallery[position]

        // remove data
        gallery.removeAt(position)
        // remove item
        notifyItemRemoved(position)

        return removedItem
    }
}

