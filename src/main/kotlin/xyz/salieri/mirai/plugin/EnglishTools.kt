package xyz.salieri.mirai.plugin

import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.syncFromEventOrNull
import xyz.salieri.english.type.Word
import xyz.salieri.english.type.defaultword
import xyz.salieri.mirai.plugin.bookData

val books: Map<String, WordTable> = mapOf(
    "CET4" to bookData.BookCET4,
    "CET6" to bookData.BookCET6,
    "TOEFL" to bookData.BookTOEFL,
    "IELTS" to bookData.BookIELTS,
    "SAT" to bookData.BookSAT,
    "GRE" to bookData.BookGRE,
    "KAOYAN" to bookData.BookKAOYAN,
)
fun randomword(name: String): Word{
    val book = books[name]
    if(book != null) {
        return book.values.random()
    } else {
        return defaultword
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
