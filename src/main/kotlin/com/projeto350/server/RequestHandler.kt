package com.projeto350.server

import com.google.gson.Gson
import com.projeto350.neo4j.Neo4jConnector
import com.projeto350.spotify.SpotifyConnector
import io.ktor.http.*

class RequestHandler (
    private val gson : Gson = Gson()
) {

    private val neo4jConnection : Neo4jConnector = Neo4jConnector()
    private val spotifyConnector : SpotifyConnector = SpotifyConnector()

    /*
        Given the parameters passed in the request, returns a bytearray representing the
        path between artists
     */
    fun handleSpotifyPath(artists: Parameters) : ByteArray {
        return byteArrayOf()
    }
}