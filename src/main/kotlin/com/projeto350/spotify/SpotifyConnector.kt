package com.projeto350.spotify

import io.ktor.client.*
import io.ktor.client.engine.cio.*

class SpotifyConnector {

    private val client : HttpClient = HttpClient(CIO)

    /*
     * Searches for the artist based on name. Returns the first artist id
     * in the spotify api response
     */
    fun getArtistId(name : String) : String {
        return ""
    }

    /*
        Returns a list of spotify uri's, each representing an artist album
     */
    fun getAlbums(artistId: String) {

    }

    /*
        Returns a list of spotify uri's, each representing an artist song
     */
    fun getTracks(artistId: String) {

    }
    /*
        Returns a list of spotify uri's, each representing an artist
     */
    fun getFeats(artistId: String) {

    }

    /*
        Return information of an artist given their spotify uri
     */
    fun getArtistById(artistId: String) {

    }
}