package com.projeto350.spotify

import ch.qos.logback.core.subst.Token
import com.projeto350.model.Artist
import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.kotlinx.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*

class SpotifyConnector {

    private val client : HttpClient = HttpClient(CIO) {
        defaultRequest {
            url("https://ktor.io/docs/")
        }
        install(ContentNegotiation) {
           json()
        }
        install(Auth) {
            bearer {
                loadTokens {
                    token = authProvider.getToken()
                    BearerTokens(accessToken = token.accessToken, refreshToken = token.expiresIn)
                }

                refreshTokens {
                    token = authProvider.getToken()
                    BearerTokens(accessToken = token.accessToken, refreshToken = token.expiresIn)
                }
            }
        }
    }

    private lateinit var token : TokenInfo
    private val authProvider: SpotifyAuthProvider = SpotifyAuthProvider()
    /*
        Search for artists based on query, returns a list of artists spotifyUri
     */
    fun searchArtists(query: String): List<String> {
        return listOf()
    }

    /*
        Returns a list of spotify uri's, each representing an artist album
     */
    fun getAlbums(artist: Artist): List<String> {
        return listOf()
    }

    /*
        Returns a list of spotify uri's, each representing an artist song
     */
    fun getTracks(artist: Artist): List<String> {
        return listOf()
    }
    /*
        Returns a list of Artist, each representing an artist the artist passed contributed
     */
    fun getFeats(artist: Artist): List<Artist> {
        return listOf()
    }

    /*
        Return an artist given their spotify uri
     */
    fun getArtistById(artistId: String): Artist {
        return Artist("name", "id")
    }
}