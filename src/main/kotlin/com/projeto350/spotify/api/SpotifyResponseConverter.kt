package com.projeto350.spotify.api

import com.google.gson.JsonArray
import com.google.gson.JsonParser
import com.projeto350.spotify.model.Album
import com.projeto350.spotify.model.Artist
import com.projeto350.spotify.model.Track

class SpotifyResponseConverter {

    fun getSearchResponseArtist(response: String): Artist {
        val json = JsonParser.parseString(response).asJsonObject
        return getArtistFromJson(json["artists"].asJsonObject["items"].asJsonArray[0].toString())
    }

    fun getArtistFromJson(response: String): Artist {
        val json = JsonParser.parseString(response).asJsonObject
        val name = json["name"].asString
        val id = json["id"].asString
        val popularity = json["popularity"]?.asInt
        return Artist(name, id, popularity)
    }

    fun getAlbumsFromJsonList(response: String): List<Album> {
        val json = JsonParser.parseString(response).asJsonObject
        val albums = json["items"].asJsonArray.map {
            album -> getAlbumFromJson(album.toString())
        }

        return albums
    }

    fun getAlbumFromJson(response: String): Album {
        val json = JsonParser.parseString(response).asJsonObject
        val name = json["name"].asString
        val id = json["id"].asString
        val artists = json["artists"].asJsonArray.map {
            artist -> getArtistFromJson(artist.toString())
        }

        return Album(name, id, artists)
    }

    fun getTracksFromJson(response: String): List<Track> {
        val json = JsonParser.parseString(response).asJsonObject
        val tracks = json["items"].asJsonArray.map {
                track -> getTrack(track.toString())
        }

        return tracks
    }

    fun getTrack(response: String): Track {
        val json = JsonParser.parseString(response).asJsonObject
        val id = json["id"].asString
        val name = json["name"].asString
        val artists = json["artists"].asJsonArray.map {
            artist -> getArtistFromJson(artist.toString())
        }

        return Track(id, name, artists)
    }
}