package com.projeto350.server

import com.google.gson.Gson
import com.projeto350.neo4j.Neo4jConnector
import com.projeto350.spotify.api.SpotifyConnector
import com.projeto350.spotify.api.SpotifyCrawler
import com.projeto350.spotify.model.Artist
import io.ktor.http.*
import kotlinx.coroutines.*

class RequestHandler (
    private val db : Neo4jConnector = Neo4jConnector(),
    private val crawler: SpotifyCrawler = SpotifyCrawler(),
    private val spotifyConnector: SpotifyConnector = SpotifyConnector.getInstance()
) {

    /*
        Given the parameters passed in the request, returns a bytearray representing the
        path between artists
     */
    suspend fun handleSpotifyPath(artists: Parameters) : Response {
        val artist1: Artist = spotifyConnector.searchArtist(artists["artist1"]!!)
        val artist2: Artist = spotifyConnector.searchArtist(artists["artist2"]!!)
        val isInDb = maybeCrawl(artist1) && maybeCrawl(artist2)
        if (isInDb) {
            val path = db.getPath(artist1, artist2)
            return Response(true, path)
        }
        return Response(false)
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun maybeCrawl(artist: Artist): Boolean {
        if (!db.isArtistInDb(artist)) {
            GlobalScope.launch {
                crawler.crawlArtistConnections(artist)
            }
            return false
        }
        return true
    }
}