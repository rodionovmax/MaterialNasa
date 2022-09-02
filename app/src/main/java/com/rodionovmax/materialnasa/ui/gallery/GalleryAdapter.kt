package com.rodionovmax.materialnasa.ui.gallery

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.rodionovmax.materialnasa.domain.model.FavoritePod

class GalleryAdapter : RecyclerView.Adapter<GalleryViewHolder>() {

    private var favouritePod: List<FavoritePod> = mutableListOf(
        FavoritePod("https://apod.nasa.gov/apod/image/2209/m51_l2.jpg", "M51: The Whirlpool Galaxy", "Fabian Neyer", "2022-09-02"),
        FavoritePod("https://apod.nasa.gov/apod/image/2209/TulipCygX-1_1024.jpg", "The Tulip and Cygnus X-1", "Peter Kohlmann", "2022-09-01"),
        FavoritePod("https://apod.nasa.gov/apod/image/2208/Jupiter2_WebbSchmidt_1080_annotated.jpg", "Jupiter from the Webb Space Telescope", null, "2022-08-30")
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder = GalleryViewHolder(parent)

    override fun onBindViewHolder(holder: GalleryViewHolder, position: Int) {
        holder.bind(favouritePod[position])
    }

    override fun getItemCount(): Int = favouritePod.size

}