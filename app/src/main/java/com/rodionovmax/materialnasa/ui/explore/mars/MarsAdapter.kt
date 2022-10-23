package com.rodionovmax.materialnasa.ui.explore.mars

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodionovmax.materialnasa.data.model.MarsPhoto

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

