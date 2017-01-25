package com.mordrum.mciv.common.models

import java.sql.Timestamp

abstract class SuperModel {
    var id: Long = 0
    var version: Long = 0
    lateinit var createdAt: Timestamp
    lateinit var updatedAt: Timestamp
}

