package com.projeto350.neo4j

import com.projeto350.spotify.model.Artist
import com.projeto350.spotify.model.Feat
import com.projeto350.spotify.model.Track
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.QueryConfig
import org.neo4j.driver.types.Node
import org.neo4j.driver.types.Path
import org.neo4j.driver.types.Relationship

class Neo4jConnector(
    dbUri: String = "neo4j://localhost",
    dbUser: String = "neo4j",
    dbPassword: String = "1024"
) {

    private val driver = GraphDatabase.driver(dbUri, AuthTokens.basic(dbUser, dbPassword))

    fun isArtistInDb(artist: Artist) : Boolean {
        val result = driver.executableQuery(
            "MATCH (a:Artist {name: \$name, id: \$id}) RETURN a")
            .withParameters(mapOf(
                "name" to artist.name,
                "id" to artist.id,
            )).withConfig(QueryConfig.builder().withDatabase("neo4j").build())
            .execute();
        return result.records().isNotEmpty()
    }

    fun insertArtist(artist: Artist) {
        driver.executableQuery(
            "MERGE (artist:Artist {name: \$name, id: \$id})")
            .withParameters(mapOf(
                "name" to artist.name,
                "id" to artist.id
            )).withConfig(QueryConfig.builder().withDatabase("neo4j").build())
            .execute();
    }

    fun insertConnection(artist1 : Artist, artist2 : Artist, track: Track) : Unit {
        driver.executableQuery(
            "MERGE (a1:Artist {name: \$a1, id: \$id1}) MERGE (a2:Artist {name: \$a2, id: \$id2}) MERGE (a1)-[:FEAT {track: \$track, id: \$trackId}]-(a2)")
            .withParameters(mapOf(
                "a1" to artist1.name,
                "a2" to artist2.name,
                "id1" to artist1.id,
                "id2" to artist2.id,
                "track" to track.name,
                "trackId" to track.id
            )).withConfig(QueryConfig.builder().withDatabase("neo4j").build())
            .execute();
    }

    fun getPath(artist1 : Artist, artist2 : Artist) : List<Feat> {
        val results = driver.executableQuery(
            "MATCH (a1:Artist {name: \$a1, id: \$id1}),(a2:Artist {name: \$a2, id: \$id2}), p = shortestPath((a1)-[*]-(a2)) RETURN p;")
            .withParameters(mapOf(
                "a1" to artist1.name,
                "a2" to artist2.name,
                "id1" to artist1.id,
                "id2" to artist2.id
            )).withConfig(QueryConfig.builder().withDatabase("neo4j").build())
            .execute();
        println(artist1)
        println(artist2)

        if (results.records().isEmpty()) return listOf()

        val records = results.records()[0]["p"].asPath()
        val response: MutableList<Feat> = mutableListOf()

        // Adiciona o primeiro artista da lista sem nenhuma musica de colaboração.
        // Considere que essa seria a contribuição de um no vazio com o primeiro da lista.
        response.add(Feat(getArtistFromNode(records.start()), Track("", "")))

        response.addAll(
            records.map {
               s -> getFeatFromSegment(s)
            }
        )

        return response
    }

    private fun getFeatFromSegment(segment: Path.Segment): Feat {
        val artist = getArtistFromNode(segment.end())
        val track =  getTrackFromRelationship(segment.relationship())
        return Feat(artist, track)
    }

    private fun getArtistFromNode(n: Node): Artist {
        return Artist(name=n["name"].asString(), id=n["id"].asString())
    }

    private fun getTrackFromRelationship(r: Relationship): Track {
        return Track(r["track"].asString(), r["trackId"].asString())
    }

    fun close() {
        driver.close()
    }
}
