package com.projeto350.server

import com.projeto350.spotify.model.Artist

data class Response(
    val hasResponse: Boolean,
    val artists: List<Artist> = listOf()
)