package xyz.salieri.mirai.plugin

import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.syncFromEventOrNull
import xyz.salieri.english.type.Word
import xyz.salieri.english.type.defaultword
import xyz.salieri.mirai.plugin.bookData

fun randomword(name: String): Word{
    return when(name) {
        "CET4" -> bookData.BookCET4.values.random()
        "CET6" -> bookData.BookCET6.values.random()
        "TOEFL" -> bookData.BookTOEFL.values.random()
        "IELTS" -> bookData.BookIELTS.values.random()
        "SAT" -> bookData.BookSAT.values.random()
        "GRE" -> bookData.BookGRE.values.random()
        "KAOYAN" -> bookData.BookKAOYAN.values.random()
        else -> defaultword
    }
}
// 将单词转化为题目
fun wordToQuestion(index: Int,total: Int,word: Word,timeLim: Long): String{
    return """
        第(${index}/${total})题，时限：[${timeLim / 1_000}秒]
        ${word.trans.joinToString("\n") {
            "[${it.pos}] ${it.tran}"
        }}
        这个单词有${word.word.length}个字母
        """.trimIndent()
}
