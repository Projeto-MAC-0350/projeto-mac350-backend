package com.projeto350.spotify.api

import com.projeto350.neo4j.Neo4jConnector
import com.projeto350.spotify.model.Artist
import java.util.*

class SpotifyCrawler(
    private val neo4jConnector: Neo4jConnector = Neo4jConnector(),
    private val spotifyConnector: SpotifyConnector = SpotifyConnector.getInstance()
) {

    suspend fun crawlArtistConnections(artist: Artist) {
        val queue: Queue<Artist> = LinkedList()
        val finished: MutableSet<Artist> = mutableSetOf()
        queue.add(artist)
        finished.add(artist)
        neo4jConnector.insertArtist(artist)
        while (queue.isNotEmpty()){
            println("queue has: ${queue.count()} artists")
            try {
                val currentArtist = queue.remove()
                val feats = spotifyConnector.getFeats(currentArtist)

                feats.forEach {
                    if (!finished.contains(it.artist)) {
                        finished.add(it.artist)
                        queue.add(it.artist)
                    }
                    neo4jConnector.insertConnection(currentArtist, it.artist, it.track)
                }
            } catch(error: Throwable) {
                throw RuntimeException("An error occurred while crawling for artist ${artist}: $error")
            }
        }
    }
}