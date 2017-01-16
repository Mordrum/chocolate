package com.mordrum.mcore.common

import com.google.gson.*
import net.minecraft.block.BlockBed
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.event.entity.player.PlayerInteractEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.PlayerEvent
import org.apache.commons.io.FileUtils
import java.io.File
import java.util.*

class BedSpawnListener {
    val gson: Gson
    private var spawnPointsMap: HashMap<UUID, WorldPosition>
    val file: File

    init {
        this.gson = Gson()
        this.spawnPointsMap = HashMap<UUID, WorldPosition>()
        this.file = File("playerSpawns.json")
        if (this.file.exists()) {
            val rawJSON = FileUtils.readFileToString(file)
            val worldPositions = JsonParser().parse(rawJSON).asJsonArray
            for (element in worldPositions) {
                val obj = element.asJsonObject
                val posObj = obj.get("pos").asJsonObject
                val pos = BlockPos(posObj.get("x").asInt, posObj.get("y").asInt, posObj.get("z").asInt)
                spawnPointsMap[UUID.fromString(obj.get("uuid").asString)] = WorldPosition(obj.get("world").asInt, pos)
            }
        }
    }

    @SubscribeEvent
    fun onPlayerUseBed(event: PlayerInteractEvent) {
        if (event.world.isRemote) return

        val blockState = event.world.getBlockState(event.pos)
        if (blockState.block is BlockBed) {
            this.spawnPointsMap[event.entityPlayer.uniqueID] = WorldPosition(event.world.provider.dimension, event.pos)
        }
    }

    @SubscribeEvent
    fun onPlayerRespawn(event: PlayerEvent.PlayerRespawnEvent) {
        val worldPosition = spawnPointsMap[event.player.uniqueID]
        if (worldPosition != null) {
            val blockPos = worldPosition.pos
            event.player.setWorld(DimensionManager.getWorld(worldPosition.world))
            event.player.attemptTeleport(blockPos.x.toDouble(), blockPos.y.toDouble(), blockPos.z.toDouble())
        }
    }

    @SubscribeEvent
    fun onModShutdown(event: ServerStoppingEvent) {
        val jsonArray = JsonArray()
        for ((uuid, worldPos) in this.spawnPointsMap) {
            val jsonObject = JsonObject()
            jsonObject.add("uuid", JsonPrimitive(uuid.toString()))

            val pos = JsonObject()
            pos.add("x", JsonPrimitive(worldPos.pos.x))
            pos.add("y", JsonPrimitive(worldPos.pos.y))
            pos.add("z", JsonPrimitive(worldPos.pos.z))
            jsonObject.add("pos", pos)

            jsonObject.add("world", JsonPrimitive(worldPos.world))
            jsonArray.add(jsonObject)
        }
        FileUtils.writeStringToFile(this.file, gson.toJson(jsonArray))
    }

    class WorldPosition(val world: Int, val pos: BlockPos)
}