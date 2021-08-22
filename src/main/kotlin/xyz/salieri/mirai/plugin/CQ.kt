package xyz.salieri.mirai.plugin

import net.mamoe.mirai.Bot
import net.mamoe.mirai.message.code.MiraiCode.deserializeMiraiCode

var bot: Bot? = null

suspend fun CQ_sendGroupMsg(ac: Int, groupId: Long, str: String) {
    bot!!.getGroup(groupId)?.sendMessage(str.deserializeMiraiCode())
}