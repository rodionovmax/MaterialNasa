package com.rodionovmax.materialnasa.ui.gallery

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
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

    fun setData(data: List<Pod>) {
//        this.data = data.toMutableList()
        gallery.clear()
        gallery.addAll(data)
        notifyDataSetChanged()
    }

    fun insertPicture(pod: Pod) {
        gallery.add(0, pod)
        notifyItemInserted(0)
    }

    fun countItems(): Int = gallery.size

    // function to implement DiffUtil
    // the problem here is that idk how to get data from viewmodel when adapter is initialized
    // when fragment calls adapter viewmodel is not initialized
    /*fun setNewData(newData: List<Pod>) {
        val diffCallback = GalleryDiffUtilCallback(data, newData)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        diffResult.dispatchUpdatesTo(this)
        gallery.clear()
        gallery.addAll(newData)
    }*/

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

