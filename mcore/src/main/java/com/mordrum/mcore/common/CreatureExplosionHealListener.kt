package com.mordrum.mcore.common

import net.minecraft.block.state.IBlockState
import net.minecraft.entity.EntityCreature
import net.minecraft.init.Blocks
import net.minecraft.util.math.BlockPos
import net.minecraftforge.common.DimensionManager
import net.minecraftforge.event.world.ExplosionEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.util.*

class CreatureExplosionHealListener {
    val snapshotMap: MutableMap<Int, MutableList<ExplosionSnapshot>>
    val timeToHeal: Long
    var timeSinceLastCheck: Long

    init {
        this.snapshotMap = HashMap()
        this.timeToHeal = 30 * 1000
        this.timeSinceLastCheck = System.currentTimeMillis()
    }

    @SubscribeEvent
    fun onCreatureExplode(event: ExplosionEvent) {
        val explosion = event.explosion
        if (explosion.explosivePlacedBy is EntityCreature) {
            // Create the map of block positions and former states before the explosion
            val affectedBlockPositions = explosion.affectedBlockPositions
            if (affectedBlockPositions.isEmpty()) return

            val blockStateMap = HashMap<BlockPos, IBlockState>()
            for (pos in affectedBlockPositions) {
                val blockState = event.world.getBlockState(pos)
                if (blockState != Blocks.AIR.defaultState) {
                    blockStateMap[pos] = blockState
                    event.world.setBlockState(pos, Blocks.AIR.defaultState)
                }
            }
            explosion.clearAffectedBlockPositions()

            // Make sure the current world has been inserted into the damage map
            val worldDimension = event.world.provider.dimension
            if (!snapshotMap.containsKey(worldDimension)) snapshotMap[worldDimension] = ArrayList()

            snapshotMap[worldDimension]?.add(ExplosionSnapshot(System.currentTimeMillis(), blockStateMap))
        }
    }

    @SubscribeEvent
    fun onWorldTick(event: TickEvent.WorldTickEvent) {
        // Only check once every second
        if (System.currentTimeMillis() - this.timeSinceLastCheck < 1000) return
        this.timeSinceLastCheck = System.currentTimeMillis()

        val worldDimension = event.world.provider.dimension
        val snapshots = snapshotMap[worldDimension] ?: return
        val time = System.currentTimeMillis()
        val toRemove = ArrayList<ExplosionSnapshot>()

        snapshots
                .asSequence()
                .filter { it.time + timeToHeal <= time }
                .forEach { snapshot ->
                    // Remove the snapshot from the list if there's no more blocks to regen
                    if (snapshot.stateMap.isEmpty()) {
                        toRemove + snapshot
                    } else {
                        var lowestBlock: BlockPos? = null
                        for ((pos) in snapshot.stateMap) {
                            if (lowestBlock == null) lowestBlock = pos
                            else if (pos.y < lowestBlock.y) lowestBlock = pos
                        }
                        if (event.world.getBlockState(lowestBlock) == Blocks.AIR.defaultState) {
                            event.world.setBlockState(lowestBlock, snapshot.stateMap[lowestBlock])
                        }
                        snapshot.stateMap.remove(lowestBlock)
                    }
                }
        snapshots.removeAll(toRemove)
    }

    @SubscribeEvent
    fun onServerStopping(event: ServerStoppingEvent) {
        for (world in DimensionManager.getWorlds()) {
            val snapshots = snapshotMap[world.provider.dimension] ?: continue
            snapshots.forEach { snapshot ->
                for ((pos) in snapshot.stateMap) {
                    if (world.getBlockState(pos) == Blocks.AIR.defaultState) {
                        world.setBlockState(pos, snapshot.stateMap[pos])
                    }
                }
            }
            snapshots.clear()
        }
    }

    class ExplosionSnapshot(val time: Long, val stateMap: MutableMap<BlockPos, IBlockState>)
}
