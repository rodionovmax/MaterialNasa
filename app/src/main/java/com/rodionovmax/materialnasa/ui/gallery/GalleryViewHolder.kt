package com.rodionovmax.materialnasa.ui.gallery

import android.annotation.SuppressLint
import android.view.*
import android.widget.ImageView
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rodionovmax.materialnasa.R
import com.rodionovmax.materialnasa.data.model.Pod
import com.rodionovmax.materialnasa.databinding.GalleryItemBinding
import com.rodionovmax.materialnasa.utils.timestampToDate
import java.lang.ref.WeakReference

class GalleryViewHolder(
    parent: ViewGroup,
) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.gallery_item, parent, false)
){
    private val binding = GalleryItemBinding.bind(itemView)

    @SuppressLint("ClickableViewAccessibility")
    fun bind(galleryItem: Pod, dragListener: GalleryListeners.OnStartDragListener) {
        if (galleryItem.bmp != null) {
            binding.galleryItemImage.setImageBitmap(galleryItem.bmp).also {
                binding.galleryItemImage.scaleType = ImageView.ScaleType.FIT_CENTER
            }
        } else {
            Glide.with(itemView.context).load(galleryItem.url).into(binding.galleryItemImage)
        }
        binding.galleryItemTitle.text = galleryItem.title
        galleryItem.copyright?.let {
            binding.galleryItemCopyright.text = galleryItem.copyright
        }

        galleryItem.date?.let {
            if (!galleryItem.date.matches("[0-9]+-[0-9]+-[0-9]+".toRegex())) {
                binding.galleryItemDate.text = timestampToDate(galleryItem.date)
            } else {
                binding.galleryItemDate.text = galleryItem.date
            }
        }

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