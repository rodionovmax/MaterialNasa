package com.rodionovmax.materialnasa.ui.gallery

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rodionovmax.materialnasa.R
import com.rodionovmax.materialnasa.data.model.Pod
import com.rodionovmax.materialnasa.databinding.GalleryItemBinding
import java.lang.ref.WeakReference

class GalleryViewHolder(
    parent: ViewGroup,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.gallery_item, parent, false)
){
    private val binding = GalleryItemBinding.bind(itemView)

    fun bind(galleryItem: Pod, dragListener: OnStartDragListener) {
        Glide.with(itemView.context).load(galleryItem.url).into(binding.galleryItemImage)
        binding.galleryItemTitle.text = galleryItem.title
        galleryItem.copyright?.let {
            binding.galleryItemCopyright.text = galleryItem.copyright
        }
        binding.galleryItemDate.text = galleryItem.date
        binding.dragHandleImage.setOnTouchListener { view, motionEvent ->
            if (motionEvent.actionMasked == MotionEvent.ACTION_DOWN) {
                dragListener.onStartDrag(this)
            }
            false
        }
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