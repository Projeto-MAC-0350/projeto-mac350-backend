package com.projeto350.spotify.api

import com.google.gson.JsonParser
import com.projeto350.spotify.model.Album
import com.projeto350.spotify.model.TokenInfo
import com.projeto350.spotify.model.Artist
import com.projeto350.spotify.model.Track
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

class SpotifyConnector {

    private val client : HttpClient = HttpClient(CIO) {
        defaultRequest {
            url("https://api.spotify.com/v1/")
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
        install(HttpRequestRetry) {
            retryOnServerErrors(maxRetries = retries)
            exponentialDelay()
        }
    }

    private val retries = 20
    private val authProvider = SpotifyAuthProvider()
    private val converter = SpotifyResponseConverter()
    private lateinit var token : TokenInfo
    /*
        Search for artist based on query, returns the first artist sent by the spotify API
     */
    suspend fun searchArtist(query: String): Artist {
        val response : String = client.get("search/") {
            url {
                parameters.append("q", query)
                parameters.append("type", "artist")
                parameters.append("market", "US")
                parameters.append("limit", "1")
                parameters.append("offset", "0")
            }
        }.body()

        return converter.getSearchResponseArtist(response)
    }

    /*
        Returns a list of spotify uri's, each representing an artist album
     */
    suspend fun getAlbums(artist: Artist): List<Album> {
        val response : String = client.get("artists") {
            url {
                appendPathSegments(artist.id, "albums")
            }
        }.body()

        return converter.getAlbumsFromJsonList(response)
    }

    suspend fun getAlbumTracks(album: Album): List<Track> {
        val response : String = client.get("albums") {
            url {
                appendPathSegments(album.id, "tracks")
                parameters.append("market", "US")
                parameters.append("limit", "50")
            }
        }.body()

        return converter.getTracksFromJson(response)
    }
    /*
        Returns a list of spotify uri's, each representing an artist song
     */
    suspend fun getTracks(artist: Artist): List<Track> {
        val albums = getAlbums(artist)
        val tracks: MutableList<Track> = mutableListOf()

        albums.forEach {
            album -> tracks.addAll(getAlbumTracks(album))
        }

        return tracks
    }
    /*
        Returns a list of Artist, each representing an artist the artist passed contributed
     */
    suspend fun getFeats(artist: Artist): Set<Artist> {
        val feats: MutableSet<Artist> = mutableSetOf()
        val tracks = getTracks(artist)
        tracks.forEach {
            track -> feats.addAll(track.artists)
        }

        return feats
    }

    /*
        Return an artist given their spotify uri
     */
    suspend fun getArtistById(artistId: String): Artist {
        val response : String = client.get("artists") {
            url {
                appendPathSegments(artistId)
            }
        }.body()
        return converter.getArtistFromJson(response)
    }
}