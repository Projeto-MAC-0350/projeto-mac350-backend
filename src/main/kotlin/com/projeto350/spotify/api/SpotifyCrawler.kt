package com.projeto350.spotify.api

import com.projeto350.neo4j.Neo4jConnector
import com.projeto350.spotify.model.Artist
import java.util.*

class SpotifyCrawler(
    private val spotifyConnector: SpotifyConnector = SpotifyConnector(),
    private val neo4jConnector: Neo4jConnector = Neo4jConnector()
) {

    suspend fun crawlArtistConnections(artist: Artist) {
        val queue: Queue<Artist> = LinkedList()
        queue.add(artist)
        neo4jConnector.insertArtist(artist)
        while (queue.isNotEmpty()){
            try {
                val currentArtist = queue.remove()
                val feats = spotifyConnector.getFeats(currentArtist)

                feats.forEach {
                    neo4jConnector.insertConnection(currentArtist, it)
                    queue.add(it)
                }
            } catch(error: Throwable) {
                println("An error occurred while crawling for artist ${artist}: $error")
            }
        }
    }
}