package com.rodionovmax.materialnasa.data.model

data class SearchResult(
    var id: Int = 0,
    var title: String = "",
    var keywords: List<String> = listOf(),
    var description: String = "",
    var imgUrl: String = ""
)
