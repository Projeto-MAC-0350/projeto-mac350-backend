package com.projeto350.plugins

import com.projeto350.server.RequestHandler
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.plugins.cors.routing.*

fun Application.configureContentNegotiation() {
    install(ContentNegotiation) {
        json()
    }
    install(CORS) {
        anyHost()
    }
}
fun Application.configureRouting() {
    val handler: RequestHandler = RequestHandler()
    routing {
        get("/path") {
            val params = call.request.queryParameters
            call.application.log.info("params: artist1=${params["artist1"]}, artist2=${params["artist2"]}")
            val response = handler.handleSpotifyPath(call.request.queryParameters)
            call.respond(response)
        }
    }
}