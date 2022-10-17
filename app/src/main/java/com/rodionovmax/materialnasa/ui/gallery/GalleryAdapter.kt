package com.rodionovmax.materialnasa.ui.gallery

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodionovmax.materialnasa.data.model.Pod

class GalleryAdapter(
    private val deleteButtonClickedListener: GalleryListeners.OnDeleteButtonClickedListener?,
    private val dragListener: GalleryListeners.OnStartDragListener,
    private val positionsListener: GalleryListeners.OnPositionsChangedListener
) : RecyclerView.Adapter<GalleryViewHolder>(), ItemTouchHelperAdapter {

    private var gallery = mutableListOf<Pod>()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder =
        GalleryViewHolder(parent)

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(gallery[position], dragListener)
        holder.updateView()

        holder.onDeleteClick = {
            val removedItem = removeItem(it)
            deleteButtonClickedListener?.removeFromDatabase(removedItem)
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

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        gallery.removeAt(fromPosition).apply {
            gallery.add(toPosition, this)
            positionsListener.updatePositionsInDb(fromPosition, toPosition, this)
        }
        notifyItemMoved(fromPosition, toPosition)
    }
}

