package xyz.salieri.mirai.plugin

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.event.GlobalEventChannel
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.utils.info
import xyz.salieri.english.type.Comps
import xyz.salieri.mirai.plugin.command.EnglishInfoCommand
import xyz.salieri.mirai.plugin.command.EnglishRandCommand

object EnglishHelperPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "xyz.salieri.mirai.plugin.english-helper",
        name = "english-helper",
        version = "1.0.0"
    ) {
        author("salieri")
    }
) {
    override fun onEnable() {
         logger.info { "单词竞赛插件已部署." }
         // 数据
         EnglishUserData.reload()
         // 命令
         EnglishInfoCommand.register()
         EnglishRandCommand.register()
         // 监听
         val eventChannel = GlobalEventChannel.parentScope(this)
         eventChannel.subscribeAlways<GroupMessageEvent> {
            if(xyz.salieri.mirai.plugin.bot == null)
                xyz.salieri.mirai.plugin.bot = bot
             Comps.mainlogic(group.id, sender.id, message.contentToString())
         }


    }

    override fun onDisable() {

        EnglishInfoCommand.unregister()
        EnglishRandCommand.unregister()
    }
}
