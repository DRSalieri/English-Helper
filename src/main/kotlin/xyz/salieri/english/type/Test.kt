package xyz.salieri.english.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Test(
    @SerialName("id")
    val id: Int,
    @SerialName("str")
    val str: List<Str>
)

@Serializable
data class Str(
    @SerialName("pos")
    val pos: String,
    @SerialName("transCn")
    val transCn: String
)