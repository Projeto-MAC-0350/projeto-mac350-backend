package com.projeto350.server

import com.projeto350.spotify.model.Feat
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Response(
    @SerialName("hasResponse") val hasResponse: Boolean,
    @SerialName("artists") val artists: List<Feat> = listOf()
)