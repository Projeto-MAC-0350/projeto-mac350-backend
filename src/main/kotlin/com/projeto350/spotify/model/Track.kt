package com.projeto350.spotify.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class Track (
    @SerialName("name") val name: String,
    @SerialName("id") val id: String,
    val artists: List<Artist> = listOf(),
)