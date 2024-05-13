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
        val finished: MutableSet<Artist> = mutableSetOf()
        queue.add(artist)
        neo4jConnector.insertArtist(artist)
        while (queue.isNotEmpty()){
            println("queue has: ${queue.count()} artists")
            try {
                val currentArtist = queue.remove()
                finished.add(currentArtist)
                val feats = spotifyConnector.getFeats(currentArtist)

                feats.forEach {
                    if (!finished.contains(it.artist)) {
                        finished.add(it.artist)
                        queue.add(it.artist)
                    }
                    neo4jConnector.insertConnection(currentArtist, it.artist, it.track)
                }
            } catch(error: Throwable) {
                println("An error occurred while crawling for artist ${artist}: $error")
                error.printStackTrace()
            }
        }
    }
}