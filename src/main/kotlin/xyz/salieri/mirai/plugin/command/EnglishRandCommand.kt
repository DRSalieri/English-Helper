package xyz.salieri.mirai.plugin.command

import kotlinx.coroutines.selects.whileSelect
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.toPlainText
import net.mamoe.mirai.event.*
import net.mamoe.mirai.event.events.GroupMessageEvent
import xyz.salieri.mirai.plugin.*
import xyz.salieri.english.type.*
import net.mamoe.mirai.message.data.*

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