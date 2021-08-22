package xyz.salieri.mirai.plugin.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import xyz.salieri.mirai.plugin.EnglishHelperPlugin
import xyz.salieri.mirai.plugin.getBooks
import xyz.salieri.mirai.plugin.randomword
import java.io.IOException

object EnglishRandCommand : SimpleCommand(
    owner = EnglishHelperPlugin,
    "随机单词",
    description = "打印一个随机的单词"
){
    @Handler
    suspend fun CommandSenderOnMessage<*>.handle(book: String) {
        try {
            sendMessage(randomword(book).toString())
        } catch (e: IOException) {
            sendMessage(
                "不存在${book}这本单词书，目前存在的单词书有：\n" + getBooks()
            )
        }
    }
}