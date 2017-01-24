package com.mordrum.mciv.common.models

import com.mordrum.mciv.common.models.embeddables.Vector2i

class CivilizationChunk : SuperModel() {
    lateinit var civilization: Civilization
    lateinit var position: Vector2i
    var claimingPlayer: Player? = null
}
