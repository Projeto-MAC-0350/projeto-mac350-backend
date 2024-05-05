package com.projeto350.spotify

import io.github.cdimascio.dotenv.dotenv
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*

class SpotifyAuthProvider {

    private val clientId: String
    private val clientSecret: String

    init {
        val dotenv = dotenv()
        clientSecret = dotenv["CLIENTSECRET"]
        clientId = dotenv["CLIENTID"]
    }

    suspend fun getToken(): TokenInfo {
        val client = HttpClient(CIO) {
            install(ContentNegotiation) {
                json()
            }
        }

        val tokenResponse: TokenInfo = client.post("https://accounts.spotify.com/api/token") {
            headers {
                append("Content-Type", "application/x-www-form-urlencoded")
            }
            setBody("grant_type=client_credentials&client_id=$clientId&client_secret=$clientSecret")
        }.body()
        println(tokenResponse)
        return tokenResponse
    }
}