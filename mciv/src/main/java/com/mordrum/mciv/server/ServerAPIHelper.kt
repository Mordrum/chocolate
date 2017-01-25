package com.mordrum.mciv.server

import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import com.mordrum.mciv.common.APIHelper
import com.mordrum.mciv.common.models.Civilization
import com.mordrum.mcore.MCore
import net.minecraft.entity.player.EntityPlayer
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import java.util.function.BiConsumer

object ServerAPIHelper: APIHelper() {
    fun invitePlayerToCivilization(playerToInvite: String, playerInviting: UUID, consumer: BiConsumer<Exception?, String>) {
        Unirest.get(MCore.API_URL + "/civilizations")
                .asJsonAsync(object : SafeCallback<String>(consumer) {
                    override fun onComplete(body: JsonNode) {
                        consumer.accept(null, body.`object`.getString("message"))
                    }
                })
    }

    fun createCivilization(name: String, banner: String, player: EntityPlayer, consumer: BiConsumer<Exception?, Civilization>) {
        val body = JSONObject()
                .put("player", player.uniqueID)
                .put("name", name)
                .put("banner", banner)
                .put("home_x", player.position.x or 4)
                .put("home_z", player.position.z or 4)

        Unirest.post(MCore.API_URL + "/civilizations")
                .body(body)
                .asJsonAsync(object : SafeCallback<Civilization>(consumer) {
                    override fun onComplete(body: JsonNode) {
                        if (this.response.status == 200) {
                            val civilization = gson.fromJson(body.toString(), Civilization::class.java)
                            consumer.accept(null, civilization)
                        } else {
                            consumer.accept(Exception(body.`object`.getString("message")), null)
                        }
                    }
                })
    }

    fun addPlayerToCivilization(player: UUID, civilizationId: Long, consumer: BiConsumer<Exception?, Civilization>) {
        val body = JSONObject()
                .put("uuid", player)
        Unirest.patch(MCore.API_URL  + "/civilizations/" + civilizationId + "/addplayer")
                .body(body)
                .asJsonAsync(object : SafeCallback<Civilization>(consumer) {
                    override fun onComplete(body: JsonNode) {
                        if (this.response.status == 200) {
                            val civilization = gson.fromJson(body.toString(), Civilization::class.java)
                            consumer.accept(null, civilization)
                        } else {
                            consumer.accept(Exception(body.`object`.getString("message")), null)
                        }
                    }
                })
    }

    fun updateCivilization(civilization: Civilization, consumer: BiConsumer<Exception?, Civilization>) {
        val body = JSONObject(gson.toJson(civilization))
        Unirest.patch(MCore.API_URL  + "/civilizations/" + civilization.id)
                .body(body)
                .asJsonAsync(object : SafeCallback<Civilization>(consumer) {
                    override fun onComplete(body: JsonNode) {
                        if (this.response.status == 200) {
                            consumer.accept(null, gson.fromJson(body.toString(), Civilization::class.java))
                        } else {
                            consumer.accept(Exception(body.`object`.getString("message")), null)
                        }
                    }
                })
    }
}