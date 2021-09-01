package xyz.salieri.mirai.plugin

import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.event.events.GroupMessageEvent
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.full.declaredMembers

object EnglishConfig : AutoSavePluginConfig("config") {
    @ValueDescription("答题时间，以秒为单位")
    var 答题时间 by value(20L)
    @ValueDescription("提示时间，以秒为单位")
    var 提示时间 by value(10L)
    @ValueDescription("同时回答的时限，以豪秒为单位")
    var 同时回答 by value(1000L)
}

object ConfigurationCommand : CompositeCommand(
    owner = EnglishHelperPlugin,
    "背单词",
    description = "背单词插件设置",
) {
    private val members = EnglishConfig::class.declaredMembers
    private suspend inline fun <reified T> set(contact: Contact, option: String, arg: T) {
        val field = members.find { it.name == option }
        if (field != null) {
            try {
                val mp = field as KMutableProperty1<EnglishConfig, T>
                mp.set(EnglishConfig, arg)
                contact.sendMessage("已将${option}设为$arg")
            } catch (e: java.lang.IllegalArgumentException) {
                contact.sendMessage("${option}不是${T::class.simpleName}型变量！")
            }
        } else {
            contact.sendMessage("不存在属性$option！")
        }
    }
    @SubCommand("禁用", "disable")
    @Description("禁用背单词选项")
    suspend fun CommandSenderOnMessage<GroupMessageEvent>.disable(option: String) {
        set(fromEvent.group, option, false)
    }
    @SubCommand("启用", "enable")
    @Description("启用背单词选项")
    suspend fun CommandSenderOnMessage<GroupMessageEvent>.enable(option: String) {
        set(fromEvent.group, option, true)
    }
    @SubCommand("设置", "set")
    @Description("设置背单词参数")
    suspend fun CommandSenderOnMessage<GroupMessageEvent>.setInt(option: String, arg: Int) {
        set(fromEvent.group, option, arg)
    }
    @SubCommand("当前设置", "settings")
    @Description("打印背单词插件设置")
    suspend fun CommandSenderOnMessage<GroupMessageEvent>.settings() {
        fromEvent.group.sendMessage(
            members.joinToString("\n") { field ->
                "◆ ${field.name}：${field.call(EnglishConfig).toString()}\n" +
                    field.annotations.filterIsInstance<ValueDescription>().joinToString { it.value }
            })
    }
}
