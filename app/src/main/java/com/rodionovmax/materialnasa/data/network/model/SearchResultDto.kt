package com.rodionovmax.materialnasa.data.network.model

import com.google.gson.annotations.SerializedName


data class SearchResultDto(
    @SerializedName("collection") val collection: CollectionDto
)

data class CollectionDto(
    @SerializedName("items") val items: List<ItemsDto?>
)

data class ItemsDto(
    @SerializedName("data") val data: List<DataDto>,
    @SerializedName("links") val links: List<LinkDto?>,
)

data class DataDto(
    @SerializedName("title") val title: String = "",
    @SerializedName("keywords") val keywords: List<String> = listOf(),
    @SerializedName("description") val description: String = ""
)

data class LinkDto(
    @SerializedName("href") val url: String = ""
)