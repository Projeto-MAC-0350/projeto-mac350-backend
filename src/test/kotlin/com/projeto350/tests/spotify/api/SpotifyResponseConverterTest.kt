package com.projeto350.tests.spotify.api

import com.projeto350.spotify.api.SpotifyResponseConverter
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SpotifyResponseConverterTest {
    private lateinit var spotifyResponseConverter: SpotifyResponseConverter
    val mockSearchResponse = """
            {
                "artists": {
                    "items": [
                        {
                            "name": "Artist 1",
                            "id": "1",
                            "popularity": 80,
                            "images": [
                                {
                                    "url": "http://example.com/image1.jpg",
                                    "height": 640,
                                    "width": 640
                                }
                            ]
                        },
                        {
                            "name": "Artist 2",
                            "id": "2",
                            "popularity": 60,
                            "images": []
                        }
                    ]
                }
            }
        """

    val mockGetArtistResponse = """
            {
                "name": "Artist 1",
                "id": "1",
                "popularity": 80,
                "images": [
                    {
                        "url": "http://example.com/image1.jpg",
                        "height": 640,
                        "width": 640
                    }
                ]
            }
        """

    val mockAlbumResponse = """
            {
                "items": [
                    {
                        "name": "Album 1",
                        "id": "1",
                        "artists": [
                            {
                                "name": "Artist 1",
                                "id": "1",
                                "popularity": 80,
                                "images": []
                            }
                        ]
                    },
                    {
                        "name": "Album 2",
                        "id": "2",
                        "artists": [
                            {
                                "name": "Artist 2",
                                "id": "2",
                                "popularity": 60,
                                "images": []
                            }
                        ]
                    }
                ]
            }
        """

    val mockTrackResponse = """
            {
                "albums": [
                    {
                        "tracks": {
                            "items": [
                                {
                                    "name": "Track 1",
                                    "id": "1",
                                    "artists": [
                                        {
                                            "name": "Artist 1",
                                            "id": "1",
                                            "popularity": 80,
                                            "images": []
                                        }
                                    ]
                                }
                            ]
                        }
                    }
                ]
            }
        """
    @BeforeEach
    fun setUp() {
        spotifyResponseConverter = SpotifyResponseConverter()
    }

    @Test
    fun `test getSearchResponseArtist with valid response`() {
        val artists = spotifyResponseConverter.getSearchResponseArtist(mockSearchResponse)
        assertEquals(2, artists.size)
        assertEquals("Artist 1", artists[0].name)
        assertEquals("1", artists[0].id)
        assertEquals(80, artists[0].popularity)
        assertNotNull(artists[0].images)
        assertEquals(1, artists[0].images?.size)
        assertEquals("Artist 2", artists[1].name)
        assertEquals("2", artists[1].id)
        assertEquals(60, artists[1].popularity)
        assertTrue(artists[1].images!!.isEmpty())
    }

    @Test
    fun `test getArtistFromJson with valid response`() {
        val artist = spotifyResponseConverter.getArtistFromJson(mockGetArtistResponse)
        assertEquals("Artist 1", artist.name)
        assertEquals("1", artist.id)
        assertEquals(80, artist.popularity)
        assertNotNull(artist.images)
        assertEquals(1, artist.images?.size)
    }

    @Test
    fun `test getAlbumsFromJsonList with valid response`() {
        val albums = spotifyResponseConverter.getAlbumsFromJsonList(mockAlbumResponse)
        assertEquals(2, albums.size)
        assertEquals("Album 1", albums[0].name)
        assertEquals("1", albums[0].id)
        assertEquals(1, albums[0].artist.size)
        assertEquals("Album 2", albums[1].name)
        assertEquals("2", albums[1].id)
        assertEquals(1, albums[1].artist.size)
    }

    @Test
    fun `test getTracksFromJson with valid response`() {
        val tracks = spotifyResponseConverter.getTracksFromJson(mockTrackResponse)
        assertEquals(1, tracks.size)
        assertEquals("Track 1", tracks[0].name)
        assertEquals("1", tracks[0].id)
        assertEquals(1, tracks[0].artists.size)
    }
}