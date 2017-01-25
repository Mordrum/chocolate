package com.mordrum.mcore.common.util

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.async.Callback
import com.mashape.unirest.http.exceptions.UnirestException
import java.util.concurrent.CancellationException
import java.util.function.BiConsumer

abstract class SafeCallback<T>(val consumer: BiConsumer<Exception?, T>) : Callback<JsonNode> {
    lateinit var response: HttpResponse<JsonNode>

    abstract fun onComplete(body: JsonNode)

    override fun completed(response: HttpResponse<JsonNode>) {
        try {
            this.response = response
            onComplete(response.body)
        } catch(e: Exception) {
            consumer.accept(e, null)
        }
    }

    override fun failed(e: UnirestException?) {
        consumer.accept(e, null)
    }

    override fun cancelled() {
        consumer.accept(CancellationException(), null)
    }
}