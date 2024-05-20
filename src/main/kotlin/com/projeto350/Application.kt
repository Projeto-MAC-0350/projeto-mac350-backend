package com.projeto350

import com.projeto350.neo4j.Neo4jConnector
import com.projeto350.plugins.*
import com.projeto350.spotify.api.SpotifyConnector
import com.projeto350.spotify.model.Artist
import com.projeto350.spotify.model.Track
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

suspend fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureContentNegotiation()
    configureRouting()
}
