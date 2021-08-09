package xyz.salieri.english

import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.serialization.decodeFromString
import java.io.File
import java.time.ZoneId
import java.util.*
/*
interface Book{
    val path: String
    val duration: Long get() = 0
}
*/

enum class Books(file: String) {
    TEST("test.json"),
    CET4("CET4.json"),
    CET6("CET6.json"),
    TOEFL("TOEFL.json"),
    IELTS("IELTS.json"),
    GRE("GRE.json"),
    SAT("SAT.json"),
    KAOYAN("KAOYAN.json");

    val path = "Books/${file}"
}


















/*
import io.ktor.http.*
import kotlinx.coroutines.delay
import java.io.File

interface GameDataType {
    val path: String                            // 路径
    val url: Url                                // url地址
    val duration: Long get() = 0                //
}

interface GameDataDownloader {
    val dir: File
    val types: Iterable<GameDataType>                                // 所有数据类型的Iterable
    suspend fun download(flush: Boolean) = types.load(dir, flush)    // 对types进行load方法
}
// 返回所有数据文件的List
suspend fun <T : GameDataType> Iterable<T>.load(dir: File, flush: Boolean): List<File> {
    return useHttpClient { client ->
        map { type ->
            dir.resolve(type.path).also { file ->
                if (flush || file.exists().not()) {
                    file.parentFile.mkdirs()
                    file.writeBytes(client.get<ByteArray>(type.url).apply { check(isNotEmpty()) })
                    delay(type.duration)
                }
            }
        }
    }
}
*/