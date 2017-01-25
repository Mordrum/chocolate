package com.mordrum.mciv.common

import com.google.gson.*
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import com.mashape.unirest.http.async.Callback
import com.mashape.unirest.http.exceptions.UnirestException
import com.mordrum.mciv.common.models.Civilization
import com.mordrum.mciv.common.models.CivilizationChunk
import com.mordrum.mciv.common.models.Player
import com.mordrum.mcore.MCore
import com.mordrum.mcore.common.util.SafeCallback
import java.sql.Timestamp
import java.util.*
import java.util.concurrent.CancellationException
import java.util.function.BiConsumer

abstract class APIHelper {
    val gson: Gson

    init {
        val builder = GsonBuilder()
        builder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        builder.setPrettyPrinting()
        builder.registerTypeAdapterFactory(object : TypeAdapterFactory {
            override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
                if (type.type !== Civilization::class.java && type.type !==  Player::class.java && type.type !==  CivilizationChunk::class.java) return null

                val delegateAdapter = gson.getDelegateAdapter(this, type)
                return MyResultObjectAdapter(delegateAdapter)
            }

        })
        builder.registerTypeAdapter(Timestamp::class.java, JsonDeserializer<Timestamp> { json, typeOfT, context ->
            Timestamp(json.asLong)
        })
        gson = builder.create()
    }

    fun getPlayer(uuid: UUID, with: Collection<String>, consumer: BiConsumer<Exception?, Player>) {
        Unirest.get(MCore.API_URL + "/players/$uuid")
                .queryString("with", with)
                .asJsonAsync(object : SafeCallback<Player>(consumer), Callback<JsonNode> {
                    override fun onComplete(body: JsonNode) {
                        val player = gson.fromJson(body.toString(), Player::class.java)
                        consumer.accept(null, player)
                    }
                })
    }

    fun getCivilizations(params: Map<String, Any>, with: Collection<String>, consumer: BiConsumer<Exception?, Array<Civilization>>) {
        Unirest.get(MCore.API_URL + "/civilizations")
                .queryString(params)
                .queryString("with", with)
                .asJsonAsync(object : SafeCallback<Array<Civilization>>(consumer) {
                    override fun onComplete(body: JsonNode) {
                        val civilizations = gson.fromJson(body.toString(), Array<Civilization>::class.java)
                        consumer.accept(null, civilizations)
                    }
                })
    }
}

class MyResultObjectAdapter<T>(delegateAdapter: TypeAdapter<T>) : TypeAdapter<T>() {
    private var defaultAdapter: TypeAdapter<T>

    init {
        this.defaultAdapter = delegateAdapter
    }

    override fun write(writer: JsonWriter, value: T) {
        defaultAdapter.write(writer, value)
    }

    override fun read(reader: JsonReader): T? {
        /*
            This is the critical part. So if the value is a string,
            Skip it (no exception) and return null.
            */
        if (reader.peek() != JsonToken.BEGIN_OBJECT) {
            reader.skipValue()
            return null
        }
        return defaultAdapter.read(reader)
    }
}
