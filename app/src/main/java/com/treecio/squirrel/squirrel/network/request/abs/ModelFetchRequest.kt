package com.treecio.squirrel.squirrel.network.request.abs

import android.content.Context
import com.treecio.squirrel.squirrel.network.Endpoint
import com.treecio.squirrel.squirrel.network.RequestMethod
import com.treecio.squirrel.squirrel.network.model.abs.JsonObject

/**
 * Uses GET method to fetch data, expecting a result of class [Response].
 */
open class ModelFetchRequest<Response : JsonObject>(
        clazz: Class<Response>,
        context: Context,
        endpoint: Endpoint
) : BaseRequest<Response>(clazz, context, endpoint, RequestMethod.GET)
