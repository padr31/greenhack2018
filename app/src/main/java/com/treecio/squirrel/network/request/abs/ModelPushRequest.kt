package com.treecio.squirrel.network.request.abs

import android.content.Context
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpResponseException
import com.google.api.client.http.json.JsonHttpContent
import com.google.api.client.json.jackson2.JacksonFactory
import com.treecio.squirrel.network.Endpoint
import com.treecio.squirrel.network.RequestMethod
import com.treecio.squirrel.network.model.abs.JsonModel
import com.treecio.squirrel.network.model.abs.JsonObject

/**
 * Uses POST method to send an entity of class [Model], expecting a result of class [Response].
 */
open class ModelPushRequest<out Model : JsonModel, Response : JsonObject>(
        clazz: Class<Response>,
        context: Context,
        endpoint: Endpoint,
        protected val entity: Model
) : BaseRequest<Response>(clazz, context, endpoint, RequestMethod.POST) {

    override fun handleHttpResponseException(e: HttpResponseException) {
        when (e.statusCode) {

            else -> super.handleHttpResponseException(e)
        }
    }

    override fun formRequest(request: HttpRequest) = request.run {
        super.formRequest(this)
        content = JsonHttpContent(JacksonFactory(), entity)
    }

}
