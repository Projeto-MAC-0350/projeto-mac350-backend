package com.projeto350.spotify

import com.projeto350.model.Artist
import com.projeto350.neo4j.Neo4jConnector

class SpotifyCrawler(
    private val spotifyConnector: SpotifyConnector = SpotifyConnector(),
    private val neo4jConnector: Neo4jConnector = Neo4jConnector()
) {

    fun crawlArtistConnections(artist: Artist) {
        neo4jConnector.insertArtist(artist)
        val feats = spotifyConnector.getFeats(artist)

        feats.forEach {
            a -> crawlArtistConnections(a)
        }
    }
}