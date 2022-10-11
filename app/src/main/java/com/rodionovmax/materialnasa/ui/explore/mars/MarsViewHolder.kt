package com.rodionovmax.materialnasa.ui.explore.mars

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewParent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rodionovmax.materialnasa.R
import com.rodionovmax.materialnasa.data.model.MarsPhoto
import com.rodionovmax.materialnasa.databinding.MarsPhotoItemBinding

class MarsViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.mars_photo_item, parent, false)
) {
    private val binding = MarsPhotoItemBinding.bind(itemView)

    fun bind(marsPhoto: MarsPhoto) {
        Glide.with(itemView.context).load(marsPhoto.imgSrc).into(binding.marsRoverPhoto)
    }
}

