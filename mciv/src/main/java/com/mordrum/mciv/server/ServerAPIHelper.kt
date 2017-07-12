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
import java.util.function.Consumer

object ServerAPIHelper: APIHelper() {
    fun invitePlayerToCivilization(playerToInvite: String, playerInviting: UUID, successHandler: Consumer<String>, errorHandler: Consumer<Exception>) {
        Unirest.get(MCore.API_URL + "/civilizations")
                .asJsonAsync(object : SafeCallback<String>(errorHandler) {
                    override fun onComplete(body: JsonNode) {
                        successHandler.accept(body.`object`.getString("message"))
                    }
                })
    }

    fun createCivilization(name: String, banner: String, player: EntityPlayer, successHandler: Consumer<Civilization>, errorHandler: Consumer<Exception>) {
        val body = JSONObject()
                .put("player", player.uniqueID)
                .put("name", name)
                .put("banner", banner)
                .put("home_x", player.position.x or 4)
                .put("home_z", player.position.z or 4)

        Unirest.post(MCore.API_URL + "/civilizations")
                .body(body)
                .asJsonAsync(object : SafeCallback<Civilization>(errorHandler) {
                    override fun onComplete(body: JsonNode) {
                        if (this.response.status == 200) {
                            val civilization = gson.fromJson(body.toString(), Civilization::class.java)
                            successHandler.accept(civilization)
                        } else {
                            errorHandler.accept(Exception(body.`object`.getString("message")))
                        }
                    }
                })
    }

    fun addPlayerToCivilization(player: UUID, civilizationId: Long, successHandler: Consumer<Civilization>, errorHandler: Consumer<Exception>) {
        val body = JSONObject()
                .put("uuid", player)
        Unirest.patch(MCore.API_URL  + "/civilizations/" + civilizationId + "/addplayer")
                .body(body)
                .asJsonAsync(object : SafeCallback<Civilization>(errorHandler) {
                    override fun onComplete(body: JsonNode) {
                        if (this.response.status == 200) {
                            val civilization = gson.fromJson(body.toString(), Civilization::class.java)
                            successHandler.accept(civilization)
                        } else {
                            errorHandler.accept(Exception(body.`object`.getString("message")))
                        }
                    }
                })
    }

    fun updateCivilization(civilization: Civilization, successHandler: Consumer<Civilization>, errorHandler: Consumer<Exception>) {
        val body = JSONObject(gson.toJson(civilization))
        Unirest.patch(MCore.API_URL  + "/civilizations/" + civilization.id)
                .body(body)
                .asJsonAsync(object : SafeCallback<Civilization>(errorHandler) {
                    override fun onComplete(body: JsonNode) {
                        if (this.response.status == 200) {
                            successHandler.accept(gson.fromJson(body.toString(), Civilization::class.java))
                        } else {
                            errorHandler.accept(Exception(body.`object`.getString("message")))
                        }
                    }
                })
    }
}