package com.treecio.squirrel

import com.google.gson.Gson
import com.treecio.squirrel.model.PlantedTree
import com.treecio.squirrel.network.ForestResponse
import okhttp3.*
import timber.log.Timber
import java.io.IOException


class NetworkClient {

    companion object {

        val JSON = MediaType.parse("application/json; charset=utf-8")

        val BASE_ENDPOINT = "http://localhost:5000"
        val ENDPOINT_FOREST = BASE_ENDPOINT + "/forest"
        val ENDPOINT_PLANT = BASE_ENDPOINT + "/plant"

    }

    var client = OkHttpClient()
    val gson = Gson()

    @Throws(IOException::class)
    fun <T> get(url: String, clazz: Class<T>, handler: (T) -> Unit) {
        Timber.d("GET: $url")
        val request = Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                e?.printStackTrace()
            }

            override fun onResponse(call: Call?, response: Response) {
                val obj = gson.fromJson(response.body()?.toString(), clazz)
                handler.invoke(obj)
            }

        })
    }

    @Throws(IOException::class)
    fun post(url: String, json: String) {
        Timber.d("POST: $url")
        val body = RequestBody.create(JSON, json)
        val request = Request.Builder()
                .url(url)
                .post(body)
                .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                e?.printStackTrace()
            }

            override fun onResponse(call: Call?, response: Response?) {
                Timber.i("Response: " + response?.body()?.string())
            }

        })
    }

    fun plant(tree: PlantedTree) {
        val json = gson.toJson(tree)
        post(ENDPOINT_PLANT, json)
    }

    fun sendFetchRequest(handler: (response: ForestResponse) -> Unit) {
        get(ENDPOINT_FOREST, ForestResponse::class.java, handler)
    }

}
