package com.treecio.squirrel.network

import com.google.api.client.http.GenericUrl
import com.treecio.squirrel.network.Endpoints.HOST

object Endpoints {

    val HOST = "http://localhost:5000"

    val PLANT = endpoint("/plant")

}

class Endpoint(stringUrl: String) {

    val url = GenericUrl(stringUrl)

}

internal fun endpoint(stringUrl: String) = Endpoint(HOST + stringUrl)
