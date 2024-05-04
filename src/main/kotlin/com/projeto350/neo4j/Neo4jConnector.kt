package com.projeto350.neo4j

import com.projeto350.model.Artist
import org.neo4j.driver.AuthTokens
import org.neo4j.driver.GraphDatabase
import org.neo4j.driver.QueryConfig

class Neo4jConnector(
    private val dbUri: String = "neo4j://localhost",
    private val dbUser: String = "neo4j",
    private val dbPassword: String = "1024"
) {

    private val driver = GraphDatabase.driver(dbUri, AuthTokens.basic(dbUser, dbPassword))
    
    fun insert_connection(artist1 : Artist, artist2 : Artist) : Unit {
        driver.executableQuery(
            "MERGE (a1:Artist {name: \$a1, id: \$id1}) MERGE (a2:Artist {name: \$a2, id: \$id2}) MERGE (a1)-[:FEAT]->(a2)")
            .withParameters(mapOf(
                "a1" to artist1.name,
                "a2" to artist2.name,
                "id1" to artist1.spotifyId,
                "id2" to artist2.spotifyId
            )).withConfig(QueryConfig.builder().withDatabase("neo4j").build())
            .execute();
    }

    fun get_path(artist1 : Artist, artist2 : Artist) : List<Artist> {
        val results = driver.executableQuery(
            "MATCH (a1:Artist {name: \$a1, id: \$id1}),(a2:Artist {name: \$a2, id: \$id2}), p = shortestPath((a1)-[*]-(a2)) RETURN p;")
            .withParameters(mapOf(
                "a1" to artist1.name,
                "a2" to artist2.name,
                "id1" to artist1.spotifyId,
                "id2" to artist2.spotifyId
            )).withConfig(QueryConfig.builder().withDatabase("neo4j").build())
            .execute();

        val records = results.records()[0]["p"].asPath().nodes()
        val response = records.map { r -> Artist(r["name"].asString(), r["id"].asString()) }
        return response
    }

    fun close() {
        driver.close()
    }
}
