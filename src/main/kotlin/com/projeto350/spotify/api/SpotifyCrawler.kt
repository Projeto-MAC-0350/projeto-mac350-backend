package com.projeto350.spotify.api

import com.projeto350.neo4j.Neo4jConnector
import com.projeto350.spotify.model.Artist

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