package xyz.salieri.mirai.plugin

import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.syncFromEventOrNull
import xyz.salieri.english.type.Word
import xyz.salieri.english.type.defaultword
import xyz.salieri.mirai.plugin.bookData

fun randomword(name: String): Word{
    if(name == "CET4")
        return bookData.BookCET4.values.random()
    else if(name == "CET6")
        return bookData.BookCET6.values.random()
    else if(name == "TOEFL")
        return bookData.BookTOEFL.values.random()
    else if(name == "IELTS")
        return bookData.BookIELTS.values.random()
    else if(name == "SAT")
        return bookData.BookSAT.values.random()
    else if(name == "GRE")
        return bookData.BookGRE.values.random()
    else if(name == "KAOYAN")
        return bookData.BookKAOYAN.values.random()
    else return defaultword
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
