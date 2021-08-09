package xyz.salieri.mirai.plugin.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.message.data.buildMessageChain
import net.mamoe.mirai.message.data.toPlainText
import xyz.salieri.mirai.plugin.*

object EnglishInfoCommand : CompositeCommand(
    owner = EnglishHelperPlugin,
    "查询",
    description = "look up for informations."
){
    @SubCommand("积分")
    @Description("look up for coin")
    suspend fun CommandSenderOnMessage<*>.coin() {
        sendMessage("Your coin: ${coin}")
    }
}