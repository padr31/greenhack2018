package com.treecio.squirrel.model

import java.util.*

data class PlantedTree(
        val id: String = UUID.randomUUID().toString(),
        val name: String,
        val story: String,
        val treetype: Int,

        val time: Long,
        val lat: Double,
        val lon: Double

)

