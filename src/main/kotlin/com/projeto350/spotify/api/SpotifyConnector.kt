package com.projeto350.spotify.api

import com.projeto350.spotify.model.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.*
import io.ktor.util.date.*
import io.ktor.utils.io.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.CoroutineContext

class SpotifyConnector {

    @OptIn(InternalAPI::class)
    private val client : HttpClient = HttpClient(CIO) {
        defaultRequest {
            url("https://api.spotify.com/v1/")
        }
        install(ContentNegotiation) {
            json()
        }
        install(Auth) {
            bearer {
                loadTokens {
                    token = authProvider.getToken()
                    BearerTokens(accessToken = token.accessToken, refreshToken = token.expiresIn)
                }

                refreshTokens {
                    token = authProvider.getToken()
                    BearerTokens(accessToken = token.accessToken, refreshToken = token.expiresIn)
                }
            }
        }
        install(HttpRequestRetry) {
            maxRetries = 20
            retryIf { _, httpResponse -> httpResponse.status != HttpStatusCode.OK }
            exponentialDelay()
        }
        install(Logging) { level = LogLevel.INFO }
    }

    val mutex = Mutex()
    private val authProvider = SpotifyAuthProvider()
    private val converter = SpotifyResponseConverter()
    private lateinit var token : TokenInfo

    init {
        client.plugin(HttpSend).intercept {
            request ->
                mutex.withLock {
                    delay(400)
                    execute(request)
                }
        }
    }
    /*
        Search for artist based on query, returns the first artist sent by the spotify API
     */
    suspend fun searchArtist(query: String): Artist {
        val response : String = client.get("search/") {
            url {
                parameters.append("q", query)
                parameters.append("type", "artist")
                parameters.append("market", "US")
                parameters.append("limit", "5")
                parameters.append("offset", "0")
            }
        }.body()
        val possibleAnswers = converter.getSearchResponseArtist(response)

        return getClosestMatch(query, possibleAnswers)
    }

    private fun getClosestMatch(query: String, artists: List<Artist>): Artist {
        var answer = Artist("", "")
        var minCost = Int.MAX_VALUE
        var cost: Int
        artists.forEach {
            cost = levenshtein(query, it.name)
            if (cost < minCost) {
                minCost = cost
                answer = it
            }
        }

        return answer
    }

    private fun levenshtein(lhs : CharSequence, rhs : CharSequence) : Int {
        val lhsLength = lhs.length + 1
        val rhsLength = rhs.length + 1

        var cost = Array(lhsLength) { it }
        var newCost = Array(lhsLength) { 0 }

        for (i in 1..rhsLength-1) {
            newCost[0] = i

            for (j in 1..lhsLength-1) {
                val match = if(lhs[j - 1] == rhs[i - 1]) 0 else 1

                val costReplace = cost[j - 1] + match
                val costInsert = cost[j] + 1
                val costDelete = newCost[j - 1] + 1

                newCost[j] = Math.min(Math.min(costInsert, costDelete), costReplace)
            }

            val swap = cost
            cost = newCost
            newCost = swap
        }

        return cost[lhsLength - 1]
    }

    /*
        Returns a list of spotify uri's, each representing an artist album
     */
    suspend fun getAlbums(artist: Artist): List<Album> {
        val response = client.get("artists") {
            url {
                appendPathSegments(artist.id, "albums")
            }
        }
        return converter.getAlbumsFromJsonList(response.body())
    }

    suspend fun getAlbumsTracks(albums: List<Album>): List<Track> {
        var ids = ""
        albums.forEach{
            ids += "${it.id},"
        }
        ids = ids.dropLast(1)
        val response : String = client.get("albums") {
            url {
                parameters.append("ids", ids)
                parameters.append("market", "US")
            }
        }.body()
        return converter.getTracksFromJson(response)
    }
    /*
        Returns a list of spotify uri's, each representing an artist song
     */
    suspend fun getTracks(artist: Artist): List<Track> {
        val albums = getAlbums(artist)
        return getAlbumsTracks(albums)
    }
    /*
        Returns a list of Artist, each representing an artist the artist passed contributed
     */
    suspend fun getFeats(artist: Artist): Set<Feat> {
        val feats: MutableSet<Feat> = mutableSetOf()
        val tracks = getTracks(artist)
        tracks.forEach {
            track ->
            if (track.artists.size > 1 && track.artists.find { a -> a.id ==  artist.id} != null) {
                feats.addAll(track.artists.map { artist -> Feat(artist, track) })
            }
        }

        return feats
    }

    /*
        Return an artist given their spotify uri
     */
    suspend fun getArtistById(artistId: String): Artist {
        val response : String = client.get("artists") {
            url {
                appendPathSegments(artistId)
            }
        }.body()
        return converter.getArtistFromJson(response)
    }
}