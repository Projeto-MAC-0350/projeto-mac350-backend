package com.projeto350.plugins

import com.projeto350.server.RequestHandler
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val handler: RequestHandler = RequestHandler()
    routing {
        get("/path") {
            val response = handler.handleSpotifyPath(call.request.queryParameters)
            call.respondBytes(response)
        }
    }
}
