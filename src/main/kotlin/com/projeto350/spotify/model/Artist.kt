package com.projeto350.spotify.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    @SerialName("name") val name: String,
    @SerialName("id") val id: String,
    @SerialName("popularity") val popularity: Int? = 0,
    @SerialName("images") val images: List<Image>? = null 
)

@Serializable
data class Image(
    @SerialName("url") val url: String,
    @SerialName("height") val height: Int,
    @SerialName("width") val width: Int
)
