package com.projeto350.spotify.model

data class Album (
    val name: String,
    val id: String,
    val artist: List<Artist>
)