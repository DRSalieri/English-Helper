package xyz.salieri.mirai.plugin

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import xyz.salieri.english.type.Word

typealias WordTable = Map<String, Word>
internal val CustomJson = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    allowStructuredMapKeys = true
}
val booksCache: MutableMap<String, WordTable> = mutableMapOf()

fun getBooks(): String {
    return EnglishHelperPlugin.dataFolder.resolve("BooksData/Books").listFiles().joinToString(" ") { it.name.replace(".json", "") }
}

fun randomword(name: String): Word {
    val path = "BooksData/Books/$name.json" // For complicity
    if (!booksCache.contains(name)) {
        println("$name loaded")
        val l: WordTable = CustomJson.decodeFromString(EnglishHelperPlugin.dataFolder.resolve(path).readText())
        booksCache[name] = l
    }
    return booksCache[name]!!.values.random()
}

// 将单词转化为题目
fun wordToQuestion(index: Int,total: Int,word: Word,timeLim: Long): String{
    return """
        #第(${index}/${total})题，时限：[${timeLim / 1_000}秒]
        ${word.trans.joinToString("\n") {
            "#[${it.pos}] ${it.tran}"
        }}
        #这个单词有${word.word.split('/')[0].length}个字母
        """.trimMargin("#")
}
