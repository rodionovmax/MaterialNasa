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

/*
class GalleryViewHolder(
    parent: ViewGroup,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.gallery_item, parent, false)
){
    private val binding = GalleryItemBinding.bind(itemView)

    fun bind(galleryItem: Pod) {
        Glide.with(itemView.context).load(galleryItem.url).into(binding.galleryItemImage)
        binding.galleryItemTitle.text = galleryItem.title
        galleryItem.copyright?.let {
            binding.galleryItemCopyright.text = galleryItem.copyright
        }
        binding.galleryItemDate.text = galleryItem.date
    }

    private val view = WeakReference(itemView)

    var onDeleteClick: ((RecyclerView.ViewHolder) -> Unit)? = null

    init {
        view.get()?.let {
            it.setOnClickListener {
                // click item to reset swiped position
                if (view.get()?.scrollX != 0) {
                    view.get()?.scrollTo(0,0)
                }
            }

            binding.removeBtn.setOnClickListener {
                onDeleteClick?.let { onDeleteClick ->
                    onDeleteClick(this)
                }
            }
        }
    }

    fun updateView() {
        // must reset swiped position because item is reused
        view.get()?.scrollTo(0, 0)
    }
}
* */