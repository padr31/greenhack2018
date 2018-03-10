package com.treecio.squirrel.network.request.abs

import android.content.Context
import android.os.Handler
import com.google.api.client.http.HttpHeaders
import com.google.api.client.http.HttpRequest
import com.google.api.client.http.HttpResponse
import com.google.api.client.http.HttpResponseException
import com.google.api.client.http.apache.ApacheHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.octo.android.robospice.request.googlehttpclient.GoogleHttpClientSpiceRequest
import com.treecio.squirrel.network.Endpoint
import com.treecio.squirrel.network.RequestMethod
import com.treecio.squirrel.network.UnparsableResponseException
import com.treecio.squirrel.network.model.abs.JsonObject
import timber.log.Timber
import java.io.IOException
import java.util.*

/**
 * Abstraction of a request to the backend. Parses the response
 */
open class BaseRequest<Response : JsonObject>(
        clazz: Class<Response>,
        protected val context: Context,
        protected val endpoint: Endpoint,
        protected val requestMethod: RequestMethod = RequestMethod.GET
) : GoogleHttpClientSpiceRequest<Response>(clazz) {

    init {
        httpRequestFactory = ApacheHttpTransport().createRequestFactory { request ->
            request.parser = JacksonFactory().createJsonObjectParser()

            formHeaders(request.headers)
            formRequest(request)
        }
    }

    override fun loadDataFromNetwork(): Response? {
        onPreExecute()

        var response: HttpResponse? = null
        var result: Response? = null
        try { // any IOException
            try { // HttpResponseException
                // create request
                val request = createRequest()

                Timber.d("Request %s: %s", requestMethod.name, endpoint.url)
                response = request.execute()
                result = response.parseAs(resultType) ?: throw UnparsableResponseException()

            } catch (e: HttpResponseException) {
                // try to handle, may be thrown further
                handleHttpResponseException(e)
            }
        } catch (ioException: IOException) {
            onUnhandledException(ioException)
        }

        if (result != null) {
            onSuccess(result)
        } else {
            onFailure()
        }

        onPostExecute(response, result)

        return result
    }

    protected open fun createRequest() = httpRequestFactory.(requestMethod.creator)(endpoint.url)

    protected open fun formHeaders(headers: HttpHeaders) {
        headers["Accept-Language"] = Locale.getDefault().language
    }

    protected open fun formRequest(request: HttpRequest) {}

    protected open fun onPreExecute() {
        Timber.d("Starting request ${getName()}")
    }

    protected open fun onPostExecute(response: HttpResponse?, result: Response?) {}
    protected open fun onSuccess(result: Response) {
        Timber.d("Request ${getName()} successful")
    }

    protected open fun onFailure() {}

    @Throws(HttpResponseException::class)
    protected open fun handleHttpResponseException(e: HttpResponseException) {
        throw e
    }

    protected open fun onUnhandledException(e: IOException) {
        Timber.e(e)
    }

    private fun getName(): String? {
        var clazz: Class<*>? = javaClass
        while (clazz != null) {
            clazz.simpleName.takeIf { it.isNotEmpty() }?.let { return it }
            clazz = clazz.superclass
        }
        return null
    }

    private fun onMainThread(block: () -> Unit) {
        context.mainLooper?.let { Handler(it) }?.post(block)
    }

}
