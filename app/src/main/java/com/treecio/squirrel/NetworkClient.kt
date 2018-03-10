package com.treecio.squirrel

import com.google.gson.Gson
import com.treecio.squirrel.model.PlantedTree
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.IOException


class NetworkClient {

    companion object {

        val JSON = MediaType.parse("application/json; charset=utf-8")

        val BASE_ENDPOINT = "http://localhost:5000"
        val ENDPOINT_PLANT = BASE_ENDPOINT + "/plant"

    }

    var client = OkHttpClient()
    val gson = Gson()

    @Throws(IOException::class)
    fun post(url: String, json: String) {
        val body = RequestBody.create(JSON, json)
        val request = Request.Builder()
                .url(url)
                .post(body)
                .build()
        val response = client.newCall(request).execute()
        println("Response: " + response.body()?.string())
    }

    fun plant(tree: PlantedTree) {
        val json = gson.toJson(tree)
        post(ENDPOINT_PLANT, json)
    }

}
