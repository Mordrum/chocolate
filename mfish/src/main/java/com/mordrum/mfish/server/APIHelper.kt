package com.mordrum.mfish.server

import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import com.mordrum.mcore.MCore
import com.mordrum.mcore.common.util.SafeCallback
import com.mordrum.mfish.common.Fish
import net.minecraft.entity.player.EntityPlayer
import org.json.JSONObject
import java.util.function.BiConsumer

object APIHelper {
    fun checkHighscore(player: EntityPlayer, fish: Fish, weight: Double, consumer: BiConsumer<Exception?, Boolean>) {
        if (player.entityWorld.isRemote) return

        val body = JSONObject()
                .put("uuid", player.uniqueID)
                .put("fish", fish.metadata)
                .put("weight", weight)

        Unirest.post(MCore.API_URL + "/fishing")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(body)
                .asJsonAsync(object: SafeCallback<Boolean>(consumer) {
                    override fun onComplete(body: JsonNode) {
                        consumer.accept(null, response.body.`object`.getBoolean("is_highscore"))
                    }
                })
    }
}