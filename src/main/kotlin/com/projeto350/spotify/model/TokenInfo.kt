package com.projeto350.spotify.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenInfo (
    @SerialName("access_token") val accessToken: String,
    @SerialName("expires_in") val expiresIn: String,
    @SerialName("token_type") val tokenType: String,
)