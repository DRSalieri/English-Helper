package xyz.salieri.mirai.plugin.command

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.CompositeCommand
import xyz.salieri.mirai.plugin.EnglishHelperPlugin
import xyz.salieri.mirai.plugin.coin

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