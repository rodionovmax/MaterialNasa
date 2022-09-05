package com.rodionovmax.materialnasa.ui.gallery

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodionovmax.materialnasa.domain.model.Pod

class GalleryAdapter : RecyclerView.Adapter<GalleryViewHolder>() {

    /*private var pod: List<Pod> = mutableListOf(
        Pod("Fabian Neyer", "2022-09-02", "", "","image","M51: The Whirlpool Galaxy", "https://apod.nasa.gov/apod/image/2209/m51_l2.jpg", true),
        Pod("Peter Kohlmann", "2022-09-01", "", "","image","The Tulip and Cygnus X-1", "https://apod.nasa.gov/apod/image/2209/TulipCygX-1_1024.jpg", true),
        Pod(null, "2022-08-30", "", "","image","Jupiter from the Webb Space Telescope", "https://apod.nasa.gov/apod/image/2208/Jupiter2_WebbSchmidt_1080_annotated.jpg", true),
    )*/
    private var gallery = mutableListOf<Pod>()

    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder = GalleryViewHolder(parent)

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(gallery[position])
        holder.updateView()

        holder.onDeleteClick = {
            removeItem(it)
        }
    }

    override fun getItemCount(): Int = gallery.size

    fun setData(list: List<Pod>) {
        gallery.clear()
        gallery.addAll(list)
        notifyDataSetChanged()
    }

    private fun removeItem(viewHolder: RecyclerView.ViewHolder) {

        val position = viewHolder.adapterPosition
        // remove data
        gallery.removeAt(position)
        // remove item
        notifyItemRemoved(position)
    }
}

