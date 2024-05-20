package com.projeto350.spotify.api

import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.projeto350.spotify.model.Album
import com.projeto350.spotify.model.Artist
import com.projeto350.spotify.model.Image
import com.projeto350.spotify.model.Track

class SpotifyResponseConverter {

    private val gson = GsonBuilder().create()

    private fun getJsonObject(response: String): JsonObject {
        val jsonObject = gson.fromJson(response, JsonObject::class.java)
        return jsonObject.asJsonObject
    }
    fun getSearchResponseArtist(response: String): List<Artist> {
        val json = getJsonObject(response)
        return json["artists"].asJsonObject["items"].asJsonArray.map {
            r -> getArtistFromJson(r.toString())
        }
    }

    fun getArtistFromJson(response: String): Artist {
        val json = getJsonObject(response)
        val name = json["name"].asString
        val id = json["id"].asString
        val popularity = json["popularity"]?.asInt
        val images: List<Image>? = json["images"]?.asJsonArray?.map { img ->
            val imageUrl = img.asJsonObject["url"].asString
            val imageHeight = img.asJsonObject["height"].asInt
            val imageWidth = img.asJsonObject["width"].asInt
            Image(imageUrl, imageHeight, imageWidth)  
        }
    
        return Artist(name, id, popularity, images)
    }

    fun getAlbumsFromJsonList(response: String): List<Album> {
        val json = getJsonObject(response)
        val albums = json["items"].asJsonArray.map {
            album -> getAlbumFromJson(album.toString())
        }

        return albums
    }

    private fun getAlbumFromJson(response: String): Album {
        val json = getJsonObject(response)
        val name = json["name"].asString
        val id = json["id"].asString
        val artists = json["artists"].asJsonArray.map {
            artist -> getArtistFromJson(artist.toString())
        }

        return Album(name, id, artists)
    }

    fun getTracksFromJson(response: String): List<Track> {
        val json = getJsonObject(response)
        val tracks: MutableList<Track> = mutableListOf()
        json["albums"].asJsonArray.forEach() {
                r -> tracks.addAll(getTracks(r.asJsonObject["tracks"].asJsonObject["items"].asJsonArray))
        }

        return tracks
    }

    private fun getTracks(tracks: JsonArray): List<Track> {
        return tracks.map {
            track -> getTrack(track.asJsonObject)
        }

    }

    private fun getTrack(track: JsonObject): Track {
        val name = track["name"].asString
        val id = track["id"].asString
        val artists = track["artists"].asJsonArray.map {
            artist -> getArtistFromJson(artist.toString())
        }

        return Track(name, id, artists)
    }
}