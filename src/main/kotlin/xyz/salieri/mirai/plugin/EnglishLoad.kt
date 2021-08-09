package xyz.salieri.mirai.plugin

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import xyz.salieri.english.Books
import xyz.salieri.english.type.*
import java.io.File

internal val CustomJson = Json {
    prettyPrint = true
    ignoreUnknownKeys = true
    isLenient = true
    allowStructuredMapKeys = true
}

private fun resolve(name: String) = EnglishHelperPlugin.dataFolder.resolve(name)

// 每个Book都单独定义read函数
internal inline fun <reified T> File.read(book: Books): T =
    CustomJson.decodeFromString(resolve(book.path).readText())

typealias TestTable = Map<String, Test>
private fun File.readTest(): TestTable = read(book = Books.TEST)

typealias WordTable = Map<String, Word>
private fun File.readCET4(): WordTable = read(book = Books.CET4)
private fun File.readCET6(): WordTable = read(book = Books.CET6)
private fun File.readTOEFL(): WordTable = read(book = Books.TOEFL)
private fun File.readIELTS(): WordTable = read(book = Books.IELTS)
private fun File.readGRE(): WordTable = read(book = Books.GRE)
private fun File.readSAT(): WordTable = read(book = Books.SAT)
private fun File.readKAOYAN(): WordTable = read(book = Books.KAOYAN)
// 用懒加载的方式读入每本书的数据

class BookData(val dir: File){
    val BookTest by lazy { dir.readTest() }
    val BookCET4 by lazy { dir.readCET4() }
    val BookCET6 by lazy { dir.readCET6() }
    val BookTOEFL by lazy { dir.readTOEFL() }
    val BookIELTS by lazy { dir.readIELTS() }
    val BookGRE by lazy { dir.readGRE() }
    val BookSAT by lazy { dir.readSAT() }
    val BookKAOYAN by lazy { dir.readKAOYAN() }
}

val BookList: List<String> = listOf(
    "CET4","CET6","TOEFL","IELTS","KAOYAN","GRE","SAT"
)

val bookData by lazy { BookData(resolve("BooksData")) }