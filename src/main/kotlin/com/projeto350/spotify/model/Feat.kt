package com.projeto350.spotify.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Feat(
    @SerialName("artist") val artist: Artist,
    @SerialName("track") val track: Track,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Feat) return false

        if (artist != other.artist) return false
        return true
    }

    override fun hashCode(): Int {
        return artist.hashCode()
    }
}