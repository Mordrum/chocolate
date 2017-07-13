package com.mordrum.mclimate.common

import java.awt.*
import java.util.GregorianCalendar

//TODO hook into BlockColors and inject Biomes O' Plenty leaves
enum class Season(foliageColor: Int, private val foliageMultiplier: Double, grassColor: Int, private val grassMultiplier: Double) {
    SPRING(0x000000, 0.0, 0x000000, 0.0), SUMMER(0x00FF00, 0.3, 0x00FF00, 0.3), AUTUMN(0x000000, 0.0, 0xFF6A00, 0.7), WINTER(0x999988, 0.9, 0x999988, 1.0);

    private val foliageColor: Color = Color(foliageColor)
    private val grassColor: Color = Color(grassColor)

    fun getFoliageColor(originalColor: Int): Int {
        return mixColors(Color(originalColor), this.foliageColor, this.foliageMultiplier)
    }

    fun getGrassColor(originalColor: Int): Int {
        return mixColors(Color(originalColor), this.grassColor, this.grassMultiplier)
    }

    private fun mixColors(baseColor: Color, colorToMix: Color, multiplier: Double): Int {
        val red = (baseColor.red * (1f - multiplier) + colorToMix.red * multiplier).toInt()
        val green = (baseColor.green * (1f - multiplier) + colorToMix.green * multiplier).toInt()
        val blue = (baseColor.blue * (1f - multiplier) + colorToMix.blue * multiplier).toInt()

        return Color(red, green, blue).rgb
    }

    fun getModifiedTemperature(temperature: Float): Float {
        if (this == SUMMER)
            return temperature * 2
        else if (this == WINTER) {
            if (temperature <= 0.8f)
                return 0.1f
            else
                return temperature / 2
        } else
            return temperature
    }

    companion object {
        val currentSeason: Season
            get() = getSeasonForTimestamp(System.currentTimeMillis())

        fun getSeasonForCalendar(calendar: GregorianCalendar): Season {
            return getSeasonForTimestamp(calendar.timeInMillis)
        }

        private fun getSeasonForTimestamp(timestamp: Long): Season {
            var mutableTimestamp = timestamp
            // Convert to seconds
            mutableTimestamp /= 1000
            // -4 hour offset
            mutableTimestamp -= (4 * 60 * 60).toLong()

            val floor = Math.floor((mutableTimestamp / 86400).toDouble()).toInt()
            val season = floor % 4
            return Season.values()[season]
        }
    }
}
