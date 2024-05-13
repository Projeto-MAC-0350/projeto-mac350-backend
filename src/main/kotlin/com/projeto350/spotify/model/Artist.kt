package com.projeto350.spotify.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Artist(
    @SerialName("name") val name: String,
    @SerialName("id") val id: String,
    @SerialName("popularity") val popularity: Int? = 0,
    @SerialName("images") val images: List<Image>? = listOf()
)