package com.projeto350

import com.projeto350.plugins.*
import com.projeto350.spotify.api.SpotifyConnector
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

suspend fun main() {
    val connector = SpotifyConnector()
    val artist = connector.searchArtist("Madonna")
    val feats = connector.getFeats(artist)
    println(feats)
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureRouting()
}
