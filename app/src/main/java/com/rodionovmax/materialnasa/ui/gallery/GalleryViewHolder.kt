package com.rodionovmax.materialnasa.ui.gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rodionovmax.materialnasa.R
import com.rodionovmax.materialnasa.databinding.GalleryItemBinding
import com.rodionovmax.materialnasa.domain.model.Pod

class GalleryViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.gallery_item, parent, false)
){
    private val binding = GalleryItemBinding.bind(itemView)

    fun bind(favoritePod: Pod) {
        Glide.with(itemView.context).load(favoritePod.url).into(binding.galleryItemImage)
        binding.galleryItemTitle.text = favoritePod.title
        favoritePod.copyright?.let {
            binding.galleryItemCopyright.text = favoritePod.copyright
        }
        binding.galleryItemDate.text = favoritePod.date
    }
}