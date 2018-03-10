package com.treecio.squirrel.squirrel.network

import com.google.api.client.http.GenericUrl
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpRequestFactory

enum class RequestMethod(val creator: HttpRequestFactory.(url: GenericUrl) -> HttpRequest) {

    GET({ buildGetRequest(it) }),
    POST({ buildPostRequest(it, null) }),
    PUT({ buildPutRequest(it, null) })

}

