package xyz.salieri.english.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Word(
    @SerialName("word")
    val word: String,
    @SerialName("trans")
    val trans: List<Tran>,
) {
    override fun toString(): String {
        return word + "\n" + trans.joinToString("\n") {
            "[${it.pos}]  ${it.tran}"
        }
    }
}

@Serializable
data class Tran(
    @SerialName("tran")
    val tran: String,
    @SerialName("pos")
    val pos: String
)

val defaultword: Word = Word("===default===", listOf(Tran("测试","n")))