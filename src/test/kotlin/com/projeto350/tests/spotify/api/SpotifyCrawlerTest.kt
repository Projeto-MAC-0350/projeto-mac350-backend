package com.projeto350.tests.spotify.api

import com.projeto350.neo4j.Neo4jConnector
import com.projeto350.spotify.api.SpotifyConnector
import com.projeto350.spotify.api.SpotifyCrawler
import com.projeto350.spotify.model.Artist
import com.projeto350.spotify.model.Feat
import com.projeto350.spotify.model.Track
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SpotifyCrawlerTest {
    @MockK(relaxed = true)
    private lateinit var neo4jConnector: Neo4jConnector
    @MockK
    private lateinit var spotifyConnector: SpotifyConnector
    @InjectMockKs
    private lateinit var spotifyCrawler: SpotifyCrawler

    private val artist = Artist("Artist1", "1")
    private val featArtist = Artist("FeatArtist", "2")
    private val track = Track("Track1", "1")
    private val feats = setOf(Feat(featArtist, track))

    @BeforeEach
    fun setUp() = MockKAnnotations.init(this)

    @AfterEach
    fun tearDown() {
        clearMocks(spotifyConnector, neo4jConnector)
    }

    @Test
    fun `should insert artist and connections successfully`() = runBlocking {
        coEvery { spotifyConnector.getFeats(featArtist) } returns setOf()
        coEvery { spotifyConnector.getFeats(artist) } returns setOf(Feat(featArtist, track))

        spotifyCrawler.crawlArtistConnections(artist)

        coVerify(exactly = 1) {spotifyConnector.getFeats(artist) }
        coVerify(exactly = 1) { spotifyConnector.getFeats(featArtist) }
        verify(exactly = 1) { neo4jConnector.insertArtist(artist) }
        verify(exactly = 1) { neo4jConnector.insertConnection(eq(artist), eq(featArtist), eq(track)) }
    }

    @Test
    fun `should handle error during crawling`(): Unit = runBlocking {
        coEvery { spotifyConnector.getFeats(artist) } throws RuntimeException("Test Exception")

        assertThrows<RuntimeException> {
            spotifyCrawler.crawlArtistConnections(artist)
        }
    }
}