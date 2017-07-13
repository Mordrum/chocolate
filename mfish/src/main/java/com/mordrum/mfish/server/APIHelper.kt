package com.mordrum.mfish.server

import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import com.mordrum.mcore.MCore
import com.mordrum.mcore.common.util.SafeCallback
import com.mordrum.mfish.common.Fish
import net.minecraft.entity.player.EntityPlayer
import org.json.JSONObject
import java.util.function.Consumer

object APIHelper {
    fun checkHighscore(player: EntityPlayer, fish: Fish, weight: Double, successHandler: Consumer<Boolean>, errorHandler: Consumer<Exception>) {
        if (player.entityWorld.isRemote) return

        val data = JSONObject()
                .put("uuid", player.uniqueID)
                .put("fish", fish.metadata)
                .put("weight", weight)

        Unirest.post(MCore.API_URL + "/fishing")
                .header("accept", "application/json")
                .header("Content-Type", "application/json")
                .body(data)
                .asJsonAsync(object: SafeCallback<Boolean>(errorHandler) {
                    override fun onComplete(body: JsonNode) {
                        successHandler.accept(response.body.`object`.getBoolean("is_highscore"))
                    }
                })
    }
}