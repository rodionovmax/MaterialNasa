package com.rodionovmax.materialnasa.ui.gallery

import androidx.recyclerview.widget.DiffUtil
import com.rodionovmax.materialnasa.data.model.Pod

// Callback is added but not used because it this sample I don't really have cases
// where only some content changes not the whole items
class GalleryDiffUtilCallback(
    private val oldList: List<Pod>,
    private val newList: List<Pod>
) : DiffUtil.Callback(){

    override fun getOldListSize(): Int = oldList.size

    override fun getNewListSize(): Int = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.javaClass == newItem.javaClass
        // or assert by some unique value in objects
        //oldList[oldItemPosition].date == newList[newItemPosition].date
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldItem = oldList[oldItemPosition]
        val newItem = newList[newItemPosition]
        return oldItem.hashCode() == newItem.hashCode()
    }
}